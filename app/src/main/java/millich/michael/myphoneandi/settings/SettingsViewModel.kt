package millich.michael.myphoneandi.settings

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.hasUsageStatsPermission

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    companion object{
        val TAG = "SettingsViewModel"
    }
    private val appContext: Context  get() {return  getApplication<Application>().applicationContext}

    fun isIgnoringBatteryOptimizationsGiven() : Boolean {
        val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = appContext.packageName
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    fun isNotificationPermissionGiven() : Boolean {
        val permissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    fun isUsageStatsPermissionGiven() : Boolean {
        var status = false
        runBlocking(Dispatchers.IO) {
            status = hasUsageStatsPermission(appContext)
            MLog.d(TAG, "permission? = $status")
        }
        return status
    }

    /**
     * Write the battery optimization status to the shared preference of the application
     */
    fun writeBooleanPref(key : String, value: Boolean){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

}