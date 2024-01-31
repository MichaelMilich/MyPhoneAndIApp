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
        val unlockEvent = ScreenEvent(eventType = ScreenEventType.ScreenOff.value)
        CoroutineScope(Dispatchers.IO).launch {
            database.Insert(unlockEvent)
            val screenOffCount =database.getTodayScreenEventCountAfterTimeNoLiveData(
                getCurrentDateInMilli(),
                eventType = ScreenEventType.ScreenOff.value
            )
            MLog.d(TAG, "updating notification : '$screenOffCount  screen off today' ")
        }
    }
}