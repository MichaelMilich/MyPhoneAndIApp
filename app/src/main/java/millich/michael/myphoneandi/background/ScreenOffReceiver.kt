package millich.michael.myphoneandi.background

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import millich.michael.myphoneandi.database.screenevents.ScreenEvent
import millich.michael.myphoneandi.database.screenevents.ScreenEventDatabase
import millich.michael.myphoneandi.database.screenevents.ScreenEventType
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.SERVICE_STOP_NOTIFICATION_LOOP
import millich.michael.myphoneandi.utils.getCurrentDateInMilli


/**
 * Broadcast Receiver for screenOff messages from the android system.
 * There is only one screenOff receiver so this is a static class - aka object.
 * This receiver will update the database when the user screen went off if the user was active.
 * NOTE, this Receiver is registered dynamically from the service since it can't be registered from the manifest.
 */
object ScreenOffReceiver : BasicBroadcastRecevier() {
    override val TAG = "ScreenOffReceiver"
    override fun onRecieveCallback(context: Context, intent: Intent) {
        MLog.d(TAG, "got msg ")
        val database = ScreenEventDatabase.getInstance(context).screenEventDatabaseDAO
        val screenOffEvent = ScreenEvent(eventType = ScreenEventType.ScreenOff.value)
        CoroutineScope(Dispatchers.IO).launch {
            val lastScreenEvent = database.getLastScreenEvent() ?: return@launch
            if (lastScreenEvent.eventType == ScreenEventType.ScreenOff.value)
                return@launch

            MLog.i(TAG,"new screen off after user active, saving to database $screenOffEvent")
            database.Insert(screenOffEvent)
            val screenOffCount =database.getTodayScreenEventCountAfterTimeNoLiveData(
                getCurrentDateInMilli(),
                eventType = ScreenEventType.ScreenOff.value
            )
            MLog.d(TAG, "updating notification : '$screenOffCount  screen off today' ")
            serviceStopNotificationJob(context)
        }
    }

    private fun serviceStopNotificationJob(context: Context){
        val intent = Intent(context, MyService::class.java)
        intent.action = SERVICE_STOP_NOTIFICATION_LOOP
        context.startService(intent)
    }
}