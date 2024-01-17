package millich.michael.myphoneandi.settings

import android.app.Application
import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.*
import androidx.preference.PreferenceManager

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val _isPermissionGiven : MutableLiveData<Boolean> = MutableLiveData(false)
    val isPermissionGiven : LiveData<Boolean>
        get() {
            val powerManager = getApplication<Application>().applicationContext.getSystemService(
                Context.POWER_SERVICE) as PowerManager
            val packageName = getApplication<Application>().applicationContext.packageName
            _isPermissionGiven.value = powerManager.isIgnoringBatteryOptimizations(packageName)
            return  _isPermissionGiven
        }

    /**
     * Write the battery optimization status to the shared preference of the application
     */
    fun writeBatteryOptimizationPref(key : String, value: Boolean){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }



    class Factory (private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown OnBoardingViewModel class")
        }
    }

}