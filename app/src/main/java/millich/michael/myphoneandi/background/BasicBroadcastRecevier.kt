package millich.michael.myphoneandi.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import millich.michael.myphoneandi.utils.MLog

/**
 * Abstract class that wraps the BroadcastReceiver to be able to safely log any error into my backup log file (Mlog)
 */
abstract class BasicBroadcastRecevier : BroadcastReceiver() {
    open val TAG = "BasicBroadcastRecevier"
    override fun onReceive(context: Context, intent: Intent) {
        try {
            MLog.d(TAG, "starting onRecieveCallback")
            onRecieveCallback(context,intent)
            MLog.d(TAG, "after onRecieveCallback is done")
        } catch (e: Exception){
            MLog.e(TAG, "found exception during onRecieveCallback")
            MLog.e(TAG, e.stackTraceToString())
        }
    }
    abstract fun onRecieveCallback(context: Context, intent: Intent)
}