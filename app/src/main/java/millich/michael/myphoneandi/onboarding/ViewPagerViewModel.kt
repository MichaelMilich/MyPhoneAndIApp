package millich.michael.myphoneandi.onboarding

import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewPagerViewModel : ViewModel() {
    var screenNumber : MutableLiveData<Int> = MutableLiveData()
    var isPermissionGiven : MutableLiveData<Boolean> = MutableLiveData()
    fun checkFirstTime(): Boolean {
        return true
    }
    fun writeOnBoarding(){

    }
}