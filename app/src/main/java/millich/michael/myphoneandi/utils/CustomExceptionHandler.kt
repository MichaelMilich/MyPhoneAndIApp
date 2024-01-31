package millich.michael.myphoneandi.utils

import android.util.Log

/**
 * A custom exception handler that will log the exception into my MLog before calling the default exception handler
 * This class is used for development purposes to be able to log the errors that happen at the end user device.
 */
class CustomExceptionHandler : Thread.UncaughtExceptionHandler {
    companion object {
        val TAG = "CustomExceptionHandler"
    }
    private val defaultUEH = Thread.getDefaultUncaughtExceptionHandler()
    override fun uncaughtException(thread: Thread, exception: Throwable) {
        try {
            MLog.e(TAG, exception.stackTraceToString())
        } catch (e : Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
        defaultUEH?.uncaughtException(thread, exception)
    }
}