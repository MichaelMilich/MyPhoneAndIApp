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
import millich.michael.myphoneandi.database.ScreenEventType
import millich.michael.myphoneandi.utils.CHANNEL_ID_1
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.ONGOING_NOTIFICATION_ID
import millich.michael.myphoneandi.utils.STOP_MY_SERVICE
import millich.michael.myphoneandi.utils.formatDateFromMillisecondsLong
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
            val newUnlock = database.getLastUnlock()
            val unlockCount =database.getTodayScreenEventCountAfterTimeNoLiveData(
                getCurrentDateInMilli()
            )
            MLog.d(TAG, "updating notification : '$unlockCount  unlocks today' ")
            showNotification(context," $unlockCount  unlocks today" ,"last time at ${formatDateFromMillisecondsLong(newUnlock!!.eventTime)}")
        }
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