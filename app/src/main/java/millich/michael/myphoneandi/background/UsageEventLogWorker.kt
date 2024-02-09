package millich.michael.myphoneandi.background

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import millich.michael.myphoneandi.database.usagestatsevent.UsageStatsEvent
import millich.michael.myphoneandi.database.usagestatsevent.UsageStatsEventDatabase
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.eventTypeToString
import millich.michael.myphoneandi.utils.formatDateFromMillisecondsLong
import millich.michael.myphoneandi.utils.getCurrentDateInMilli
import millich.michael.myphoneandi.utils.hasUsageStatsPermission
import kotlin.math.max

class UsageEventLogWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams)  {

    private  var usageStatsManager :UsageStatsManager? =null
    private val database = UsageStatsEventDatabase.getInstance(appContext).usageStatsEventDatabaseDAO
    companion object {
        const val TAG = "UsageEventLogWorker"
    }

    init {
        MLog.d(TAG,"worker init")
    }
    override fun doWork(): Result {
        MLog.i(TAG,"starting to do work")
        if (!hasUsageStatsPermission(applicationContext)) {
            MLog.w(TAG,"[usage stats] permission not granted not collecting info")
            return Result.failure()
        }
        if (usageStatsManager == null)
            usageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        CoroutineScope(Dispatchers.IO).launch {
            MLog.i(TAG,"[usage stats] permission  granted  collecting info")
            val lastHandledTimestamp : Long = database.getLastEvent()?.timestamp ?: 0L
            val startTime = max(lastHandledTimestamp, getCurrentDateInMilli())
            val stopTime = System.currentTimeMillis()
            val events = usageStatsManager?.queryEvents(startTime, stopTime)
            MLog.i(TAG,"[usage stats] what is events  = $events")
            MLog.i(TAG,"[usage stats] is events null?  = ${events==null}")
            val event = UsageEvents.Event()
            while (events?.hasNextEvent() ==true) {
                events.getNextEvent(event)
                MLog.i(TAG,"[usage stats] event package = ${event.packageName} at  ${formatDateFromMillisecondsLong(event.timeStamp)} , event type = ${event.eventTypeToString()}")
                database.Insert(UsageStatsEvent.fromUsageEvent(event))
                // Analyze the event
            }
        }
        return Result.success()
    }
}