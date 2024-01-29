package millich.michael.myphoneandi.background


import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import millich.michael.myphoneandi.*
import millich.michael.myphoneandi.database.UnlockDatabase
import millich.michael.myphoneandi.database.UnlockDatabaseDAO
import millich.michael.myphoneandi.utils.CHANNEL_ID_1
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.ONGOING_NOTIFICATION_ID
import millich.michael.myphoneandi.utils.STOP_MY_SERVICE
import millich.michael.myphoneandi.utils.getCurrentDateInMilli

/**
 * The service that runs with the application.
 * Does not run on it's own process but uses corutines for any action
 */
class MyService: Service()  {
    companion object {
        val TAG = "MyService"
    }
    // creating the interface for the connection between the servie and viewmodel.
    inner class LocalBinder : Binder() {
        fun getService() : MyService =this@MyService
    }
    private val binder = LocalBinder()
    lateinit var database: UnlockDatabaseDAO
    var isServiceRunning =false // if the service is already running, don't create another broadcast receiver and don't show new notifications
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /**
     * Set up the service
     */
    override fun onCreate() {
        super.onCreate()
        MLog.i(TAG, "inside myservice on create")
        val application = requireNotNull(this).application
        database = UnlockDatabase.getInstance(application).unlockDatabaseDAO
        isServiceRunning=false
    }

    /**
     * The function called when starting the service.
     * This function is called via many parts of the app.
     * if we want to stop the service from notification we send an intent to this function with STOP_MY_SERVICE extra.
     * Else the service is eaither runing already so we dont need to do anything or we should set up the broadcast reciever from corutines.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MLog.w(TAG, "[onStartCommand] intent = $intent, flags = $flags, startId = $startId")
        if(STOP_MY_SERVICE == intent?.action)
        {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        if(isServiceRunning) {
            return START_STICKY
        }
        CoroutineScope(Dispatchers.IO).launch {
            val unlockCount =database.getTodayUnlocksCountAfterTimeNoLiveData(getCurrentDateInMilli())
            showNotificationAndStartForeground(" $unlockCount  unlocks today" , "")
        }
//        runBlocking { launch {
//            val unlockCount =database.getTodayUnlocksCountAfterTimeNoLiveData(getCurrentDateInMilli())
//            showNotificationAndStartForeground(" $unlockCount  unlocks today" , "")
//        } }
        MLog.i(TAG, "inside MyService onStartCommand")
        registerReceiver(UnlockBroadcastReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
        isServiceRunning=true
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean(resources.getString(R.string.background_service_run),true)
        editor.apply()

        return START_STICKY
    }

    /**
     * Claen the service and destroy
     * Dont froget to unregister reciver.
     */
    override fun onDestroy() {
        isServiceRunning=false
        stopForeground(true)
        unregisterReceiver(UnlockBroadcastReceiver)
        super.onDestroy()
    }


     private fun showNotificationAndStartForeground(title: String, message: String) {
         MLog.i(TAG, "showNotificationAndStartForeground - starting notification")
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(applicationContext, MyService::class.java)
        stopIntent.action= STOP_MY_SERVICE
        val pendingStopIntent = PendingIntent.getService(
            applicationContext, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_my_phone_and_i_notification_option2) // notification icon
            .setContentTitle(title) // title for notification
            .setContentText(message)// message for notification
            .setContentIntent(pendingIntent)
            .setContentText(message)// message for notification
            .addAction(R.drawable.ic_my_phone_and_i_notification_option2,applicationContext.resources.getString(R.string.stop_service),pendingStopIntent)
            .setOngoing(true)
            .build()

         ServiceCompat.startForeground( this,
              ONGOING_NOTIFICATION_ID,
             notification ,
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
             } else {
                 0
             })
        startForeground(ONGOING_NOTIFICATION_ID,notification)
    }


}