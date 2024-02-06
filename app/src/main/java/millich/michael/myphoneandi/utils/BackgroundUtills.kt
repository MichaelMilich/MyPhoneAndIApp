package millich.michael.myphoneandi.utils

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.content.Context
import millich.michael.myphoneandi.database.ScreenEventDatabaseDAO
import millich.michael.myphoneandi.database.ScreenEventType

/**
 * Calculates the time the user used the phone.
 * We call this calculation here and not in screenOff broadcast receiver because the notification should be called when the user is active.
 * The function calculates the time using this equation:
 * phone_time = sum_timestamp_screenOff + current_timestamp - sum_timestamp_screenOn
 * For that we need to find the time of the first screenOn and calculate from there.
 * This is because sometimes when installing the app a screenOff will happen before ScreenOn.
 */
fun calculateTodayPhoneTime(database: ScreenEventDatabaseDAO,firstCallTime : Long, tag: String) : String{
    val firstScreenOnTimeForToday = database.getTimeOfFirstUnlockFromTime(getCurrentDateInMilli())
    val screenOnTimestampSum = database.getSumOfTimestampsFromTime(getCurrentDateInMilli(), ScreenEventType.ScreenOn.value)?: firstCallTime

    val screenOffTimestampSum = firstScreenOnTimeForToday?.let {
        database.getSumOfTimestampsFromTime(
            it, ScreenEventType.ScreenOff.value )
    }
    val timeSpentMilliseconds = System.currentTimeMillis()+ (screenOffTimestampSum?: 0L) - screenOnTimestampSum
//    MLog.d(tag, "firstScreenOnTimeForToday = $firstScreenOnTimeForToday")
//    MLog.d(tag, "screenOnTimestampSum = $screenOnTimestampSum")
//    MLog.d(tag, "screenOffTimestampSum = $screenOffTimestampSum")
//    MLog.d(tag, "System.currentTimeMillis() = ${(System.currentTimeMillis())}")
//    MLog.d(tag, "time spent on phone in milliseconds = $timeSpentMilliseconds")
    return formatDuration(timeSpentMilliseconds)
}

fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(), context.packageName)
    MLog.d("Usage_Stats", "usage stats permissions = $mode")
    MLog.d("Usage_Stats", "is permission granted? = ${mode == AppOpsManager.MODE_ALLOWED}")
    return mode == AppOpsManager.MODE_ALLOWED
}

fun UsageEvents.Event.eventTypeToString(): String {
    return when (this.eventType) {
        UsageEvents.Event.ACTIVITY_PAUSED -> "ACTIVITY_PAUSED"
        UsageEvents.Event.ACTIVITY_RESUMED -> "ACTIVITY_RESUMED"
        UsageEvents.Event.ACTIVITY_STOPPED -> "ACTIVITY_STOPPED"
        UsageEvents.Event.CONFIGURATION_CHANGE -> "CONFIGURATION_CHANGE"
        UsageEvents.Event.DEVICE_SHUTDOWN -> "DEVICE_SHUTDOWN"
        UsageEvents.Event.DEVICE_STARTUP -> "DEVICE_STARTUP"
        UsageEvents.Event.FOREGROUND_SERVICE_START -> "FOREGROUND_SERVICE_START"
        UsageEvents.Event.FOREGROUND_SERVICE_STOP -> "FOREGROUND_SERVICE_STOP"
        UsageEvents.Event.KEYGUARD_HIDDEN -> "KEYGUARD_HIDDEN"
        UsageEvents.Event.KEYGUARD_SHOWN -> "KEYGUARD_SHOWN"
        UsageEvents.Event.NONE -> "NONE"
        UsageEvents.Event.SCREEN_INTERACTIVE -> "SCREEN_INTERACTIVE"
        UsageEvents.Event.SCREEN_NON_INTERACTIVE -> "SCREEN_NON_INTERACTIVE"
        UsageEvents.Event.SHORTCUT_INVOCATION -> "SHORTCUT_INVOCATION"
        UsageEvents.Event.STANDBY_BUCKET_CHANGED -> "STANDBY_BUCKET_CHANGED"
        UsageEvents.Event.USER_INTERACTION -> "USER_INTERACTION"
        // Add other event types here
        else -> "UNKNOWN_EVENT_TYPE"
    }
}

