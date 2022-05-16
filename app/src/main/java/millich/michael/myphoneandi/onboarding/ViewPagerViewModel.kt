package millich.michael.myphoneandi.onboarding

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewPagerViewModel(application: Application) : AndroidViewModel(application) {
    var screenNumber : MutableLiveData<Int> = MutableLiveData()
    var isPermissionGiven : MutableLiveData<Boolean> = MutableLiveData(false)
    fun checkFirstTime(): Boolean {
        return true
    }
    fun writeOnBoarding(){

    }
     fun testBatteryOptimization(){
        val intent = Intent()
        val powerManager = getApplication<Application>().applicationContext.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = getApplication<Application>().applicationContext.applicationContext.packageName
         isPermissionGiven.value = powerManager.isIgnoringBatteryOptimizations(packageName)
    }
}