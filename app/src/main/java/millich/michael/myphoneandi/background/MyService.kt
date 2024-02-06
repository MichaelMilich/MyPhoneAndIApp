package millich.michael.myphoneandi.background


import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import millich.michael.myphoneandi.MainActivity
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.database.ScreenEventDatabase
import millich.michael.myphoneandi.database.ScreenEventDatabaseDAO
import millich.michael.myphoneandi.utils.CHANNEL_ID_1
import millich.michael.myphoneandi.utils.CustomExceptionHandler
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.ONGOING_NOTIFICATION_ID
import millich.michael.myphoneandi.utils.SERVICE_START_NOTIFICATION_LOOP
import millich.michael.myphoneandi.utils.SERVICE_STOP_NOTIFICATION_LOOP
import millich.michael.myphoneandi.utils.STOP_MY_SERVICE
import millich.michael.myphoneandi.utils.calculateTodayPhoneTime
import millich.michael.myphoneandi.utils.formatDateFromMillisecondsLong
import millich.michael.myphoneandi.utils.getCurrentDateInMilli
import millich.michael.myphoneandi.utils.hasUsageStatsPermission

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
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private val binder = LocalBinder()
    lateinit var database: ScreenEventDatabaseDAO
    private var notificationJob : Job? =null
    private var isServiceRunning =false // if the service is already running, don't create another broadcast receiver and don't show new notifications
    private  var usageStatsManager :UsageStatsManager? =null


    /**
     * Set up the service
     */
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler()) // setting up the custom exception handler for the Mlog
        // this is a new thread that might be different from the applications thread, so make sure we log it as well.
        MLog.i(TAG, "inside myservice on create")
        val application = requireNotNull(this).application
        database = ScreenEventDatabase.getInstance(application).screenEventDatabaseDAO
        isServiceRunning=false
    }

    /**
     * The function called when starting the service.
     * This function is called via many parts of the app.
     * if we want to stop the service from notification we send an intent to this function with STOP_MY_SERVICE extra.
     * If the service is already running we dont do anything.
     * However if it is the first time we start the service, we need to register the BroadcastReceivers.
     * (These BroadcastReceivers can't be registered in the android manifest since Android 8)
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MLog.w(TAG, "[onStartCommand] intent = $intent, flags = $flags, startId = $startId")
        intent?.let {
            when(it.action){
                SERVICE_STOP_NOTIFICATION_LOOP -> notificationLoop(true)
                SERVICE_START_NOTIFICATION_LOOP -> notificationLoop(false)
                STOP_MY_SERVICE -> {
                    stopSelf()
                    return super.onStartCommand(intent, flags, startId)
                }
            }
        }
        if(isServiceRunning) {
            return START_STICKY
        }
        CoroutineScope(Dispatchers.IO).launch {
            val unlockCount =database.getTodayScreenEventCountAfterTimeNoLiveData(getCurrentDateInMilli())
            val timeToday = calculateTodayPhoneTime(database, System.currentTimeMillis(),TAG)
            showNotificationAndStartForeground("$timeToday on the phone today " , "$unlockCount  unlocks today")
            notificationLoop(false)
        }
        MLog.i(TAG, "inside MyService onStartCommand")
        registerReceiver(UnlockBroadcastReceiver, IntentFilter(Intent.ACTION_USER_PRESENT))
        registerReceiver(ScreenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
        isServiceRunning=true
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putBoolean(resources.getString(R.string.background_service_run),true)
        editor.apply()

        return START_STICKY
    }

    private fun notificationLoop(shouldStop : Boolean){
        val status = if (shouldStop) "stop" else "start"
        MLog.d(TAG, "calling to $status the notification loop")
        if (shouldStop)
            notificationJob?.cancel()
        else if (!shouldStop){
            notificationJob = CoroutineScope(Dispatchers.IO).launch {
                val firstCallTime = System.currentTimeMillis()
                while (true){
                    val timestampStart = System.currentTimeMillis()
                    delay(1000*60) // delay for a whole minute
                    val timestampStop = System.currentTimeMillis()
                    val unlockCount =database.getTodayScreenEventCountAfterTimeNoLiveData(getCurrentDateInMilli())
                    val timeToday = calculateTodayPhoneTime(database,firstCallTime, TAG)
                    val title = "$timeToday on the phone today "
                    val message = "$unlockCount  unlocks today"
                    MLog.d(TAG, "[notificationLoop] updating the notification with title ='$title', message = '$message'")
                    showNotification(title , message)
                    logUserActivity(timestampStart,timestampStop)
                }
            }
        }
    }

    /**
     * Clean the service and destroy
     * Don't forget to unregister receiver.
     */
    override fun onDestroy() {
        isServiceRunning=false
        stopForeground(true)
        unregisterReceiver(UnlockBroadcastReceiver)
        unregisterReceiver(ScreenOffReceiver)
        notificationJob?.cancel()
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

    private fun showNotification( title: String, message: String) {
        val mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(applicationContext, MyService::class.java)
        stopIntent.action= STOP_MY_SERVICE
        val pendingStopIntent = PendingIntent.getService(applicationContext,0,stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_my_phone_and_i_notification_option2) // notification icon
            .setContentTitle(title) // title for notification
            .setContentText(message)// message for notification
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_my_phone_and_i_notification_option2,applicationContext.resources.getString(R.string.stop_service),pendingStopIntent)
            .setOngoing(true)
            .build()
        mNotificationManager.notify(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun logUserActivity(startTime : Long , stopTime :Long){
        if (usageStatsManager == null)
            usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        if (hasUsageStatsPermission(applicationContext)) {
            MLog.w(TAG,"[usage stats] permission not granted not collecting info")
            return
        }
//        MLog.i(TAG,"[usage stats] permission  granted  collecting info")
//        val usageStatsList = usageStatsManager?.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, stopTime)
//        MLog.d(TAG,"[usage stats] between ${formatDateFromMillisecondsLong(startTime)} and ${formatDateFromMillisecondsLong(stopTime)}")
//        MLog.d(TAG, "is usageStatsList null ? = ${usageStatsList==null}")
//        MLog.d(TAG, "what is usageStatsList  = $usageStatsList")
//        usageStatsList?.forEach { usageStat ->
//            MLog.d(TAG,"[usage stats] Package name: ${usageStat.packageName}")
//            MLog.d(TAG,"[usage stats] Total time in foreground: ${usageStat.totalTimeInForeground}")
//        }
        MLog.i(TAG,"[usage stats] permission  granted  collecting info")
        val events = usageStatsManager?.queryEvents(startTime, stopTime)
        MLog.i(TAG,"[usage stats] what is events  = $events")
        MLog.i(TAG,"[usage stats] is ebvents null?  = ${events==null}")
        val event = UsageEvents.Event()
        while (events?.hasNextEvent() ==true) {
            events.getNextEvent(event)
            MLog.i(TAG,"[usage stats] event package = ${event.packageName} at timestamp = ${event.timeStamp} , event type = ${event.eventType}")
            // Analyze the event
        }
    }


}