package millich.michael.myphoneandi

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import millich.michael.myphoneandi.utils.CustomExceptionHandler
import millich.michael.myphoneandi.utils.MLog

@HiltAndroidApp
class MyApplication : Application() {

    companion object{
        val TAG="MyApplication"
    }
    override fun onCreate() {
        super.onCreate()
        MLog.initialize(applicationContext) // setting up the MLog to log into a txt file the results.
        MLog.i(TAG, "STARTING THE APPLICATION")
        Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler()) // setting up the custom exception handler for the Mlog
    }
}