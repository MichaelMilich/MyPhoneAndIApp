package millich.michael.myphoneandi.background


import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import millich.michael.myphoneandi.*
import millich.michael.myphoneandi.database.UnlockDatabase
import millich.michael.myphoneandi.database.UnlockDatabaseDAO

/**
 * The service that runs with the application.
 * Does not run on it's own process but uses corutines for any action
 */
class MyService: Service() {
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
        if(STOP_MY_SERVICE == intent!!.action)
        {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }

        if(isServiceRunning) {
            return super.onStartCommand(intent, flags, startId)
        }

        runBlocking { launch {
            val unlockCount =database.getTodayUnlocksCountAfterTimeNoLiveData(getCurrentDateInMilli())
            showNotificationAndStartForeground(" $unlockCount  unlocks today" , "")
        } }
        registerReceiver(UnlockBroadcastReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
        isServiceRunning=true
        return super.onStartCommand(intent, flags, startId)
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
            .addAction(R.drawable.ic_my_phone_and_i_notification_option2,applicationContext.resources.getString(R.string.stop_service),pendingStopIntent)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID,notification)
    }


}