package millich.michael.myphoneandi.background

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import millich.michael.myphoneandi.MainActivity
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.database.ScreenEventDatabase
import millich.michael.myphoneandi.database.ScreenEvent
import millich.michael.myphoneandi.database.ScreenEventDatabaseDAO
import millich.michael.myphoneandi.database.ScreenEventType
import millich.michael.myphoneandi.utils.CHANNEL_ID_1
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.ONGOING_NOTIFICATION_ID
import millich.michael.myphoneandi.utils.STOP_MY_SERVICE
import millich.michael.myphoneandi.utils.formatDateFromMillisecondsLong
import millich.michael.myphoneandi.utils.formatDuration
import millich.michael.myphoneandi.utils.getCurrentDateInMilli

/**
 * todo: completely document this static class.
 * NOTE, this Receiver is registered dynamically from the service since it can't be registered from the manifest.
 */
object UnlockBroadcastReceiver : BasicBroadcastRecevier() {

    override val TAG = "UnlockBroadcastReceiver"
    override fun onRecieveCallback(context: Context, intent: Intent) {
        val database = ScreenEventDatabase.getInstance(context).screenEventDatabaseDAO
        val unlockEvent = ScreenEvent(eventType = ScreenEventType.ScreenOn.value)
        CoroutineScope(Dispatchers.IO).launch {
            database.Insert(unlockEvent)

            val timeToday = calculateTodayPhoneTime(database)
            val newUnlockTime = database.getLastUnlock()?.eventTime ?: System.currentTimeMillis()
            MLog.d(TAG, "updating notification : 'phone time today = $timeToday' ")
            showNotification(context,"$timeToday on the phone today " ,"last unlock time at ${formatDateFromMillisecondsLong(newUnlockTime)}")
        }
    }

    /**
     * Calculates the time the user used the phone.
     * We call this calculation here and not in screenOff broadcast receiver because the notification should be called when the user is active.
     * The function calculates the time using this equation:
     * phone_time = sum_timestamp_screenOff + current_timestamp - sum_timestamp_screenOn
     * For that we need to find the time of the first screenOn and calculate from there.
     * This is because sometimes when installing the app a screenOff will happen before ScreenOn.
     * todo: make this a utill function that runs on Dispatcher.IO - I might use it in another places.
     */
    private fun calculateTodayPhoneTime(database: ScreenEventDatabaseDAO) : String{
        val firstScreenOnTimeForToday = database.getTimeOfFirstUnlockFromTime(getCurrentDateInMilli())
        val screenOnTimestampSum = database.getSumOfTimestampsFromTime(getCurrentDateInMilli(), ScreenEventType.ScreenOn.value)?: System.currentTimeMillis()

        val screenOffTimestampSum = firstScreenOnTimeForToday?.let {
            database.getSumOfTimestampsFromTime(
                it,ScreenEventType.ScreenOff.value )
        }
        val timeSpentMilliseconds = System.currentTimeMillis()+ (screenOffTimestampSum?: 0L) - screenOnTimestampSum
        MLog.d(TAG, "firstScreenOnTimeForToday = $firstScreenOnTimeForToday")
        MLog.d(TAG, "screenOnTimestampSum = $screenOnTimestampSum")
        MLog.d(TAG, "screenOffTimestampSum = $screenOffTimestampSum")
        MLog.d(TAG, "System.currentTimeMillis() = ${(System.currentTimeMillis())}")
        MLog.d(TAG, "time spent on phone in milliseconds = $timeSpentMilliseconds")
        return formatDuration(timeSpentMilliseconds)
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(context, MyService::class.java)
        stopIntent.action= STOP_MY_SERVICE
        val pendingStopIntent = PendingIntent.getService(context,0,stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_my_phone_and_i_notification_option2) // notification icon
            .setContentTitle(title) // title for notification
            .setContentText(message)// message for notification
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_my_phone_and_i_notification_option2,context.resources.getString(R.string.stop_service),pendingStopIntent)
            .setOngoing(true)
            .build()
        mNotificationManager.notify(ONGOING_NOTIFICATION_ID, notification)
    }
}