package millich.michael.myphoneandi.onboarding

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager

private const val YOUTUBE_URL = "https://www.youtube.com/watch?v=kZMLz1Fctog"
/**
 * The ViewModel for all the onBoarding Process.
 * This viewModel will help the navigation between the different screens.
 * The viewPagerFraqgment also observes the LiveData of this ViewModel.
 * The logic of this viewModel is:
 * 1) save the required screen number to be shown.
 * 2) check the status of the permission given by the user
 * 3) write and read if the user already passed the OnBoarding each time the application starts.
 */
class ViewPagerViewModel(application: Application) : AndroidViewModel(application) {
    var screenNumber : MutableLiveData<Int> = MutableLiveData(0)
    var isPermissionGiven : MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * The check function for the battery optimization of the application.
     * Battery optimization required permissions from the power manger and not the regular permissions API
     */
     fun testBatteryOptimization(){
        val powerManager = getApplication<Application>().applicationContext.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = getApplication<Application>().applicationContext.applicationContext.packageName
         isPermissionGiven.value = powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    /**
     * Write the OnBoarding status to the shared preference of the application
     */
    fun writeOnBoardingFinished(){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean("Finished",true)
        editor.apply()
    }

    /**
     * Read The OnBoarding status from the shared preference of the application
     */
    fun isOnBoardingFinished() : Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication<Application>().applicationContext)
        return sharedPreferences.getBoolean("Finished",false)
    }

    fun openYoutubeVideoExample(){
        val yt_play = Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL))
        yt_play.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        getApplication<Application>().applicationContext.applicationContext.startActivity(yt_play)
    }

}