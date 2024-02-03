package millich.michael.myphoneandi.background

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import millich.michael.myphoneandi.database.ScreenEvent
import millich.michael.myphoneandi.database.ScreenEventDatabase
import millich.michael.myphoneandi.database.ScreenEventType
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.getCurrentDateInMilli

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
        }
    }
}