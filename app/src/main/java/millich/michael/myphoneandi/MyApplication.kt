package millich.michael.myphoneandi

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import millich.michael.myphoneandi.utils.MLog

@HiltAndroidApp
class MyApplication : Application() {

    companion object{
        val TAG="MyApplication"
    }
    override fun onCreate() {
        super.onCreate()
        MLog.initialize(applicationContext)
        MLog.i(TAG, "STARTING THE APPLICATION")
    }
}