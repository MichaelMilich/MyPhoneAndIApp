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

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    companion object{
        val TAG = "SettingsViewModel"
    }
    private val appContext: Context  get() {return  getApplication<Application>().applicationContext}
    private var _isIgnoringBatteryOptimizationsGiven : MutableLiveData<Boolean> = MutableLiveData(false)
    val isIgnoringBatteryOptimizationsGiven : LiveData<Boolean>
        get() {
            val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = appContext.packageName
            _isIgnoringBatteryOptimizationsGiven.value = powerManager.isIgnoringBatteryOptimizations(packageName)
            return  _isIgnoringBatteryOptimizationsGiven
        }
    private var _isNotificationPermissionGiven = MutableLiveData(false)
    val isNotificationPermissionGiven : LiveData<Boolean>
        get() {
            val permissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            } else {
                PackageManager.PERMISSION_GRANTED
            }
            _isNotificationPermissionGiven.value =
                permissionStatus == PackageManager.PERMISSION_GRANTED
            return _isNotificationPermissionGiven
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