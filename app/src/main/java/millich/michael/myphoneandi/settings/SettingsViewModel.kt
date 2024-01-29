package millich.michael.myphoneandi.settings

import android.app.Application
import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    companion object{
        val TAG = "SettingsViewModel"
    }
    private val appContext: Context  get() {return  getApplication<Application>().applicationContext}
    private val _isIgnoringBatteryOptimizationsGiven : MutableLiveData<Boolean> = MutableLiveData(false)
    val isIgnoringBatteryOptimizationsGiven : LiveData<Boolean>
        get() {
            val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = appContext.packageName
            _isIgnoringBatteryOptimizationsGiven.value = powerManager.isIgnoringBatteryOptimizations(packageName)
            return  _isIgnoringBatteryOptimizationsGiven
        }

    /**
     * Write the battery optimization status to the shared preference of the application
     */
    fun writeBatteryOptimizationPref(key : String, value: Boolean){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

}