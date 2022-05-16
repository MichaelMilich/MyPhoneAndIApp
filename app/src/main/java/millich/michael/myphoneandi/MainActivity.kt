package millich.michael.myphoneandi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import millich.michael.myphoneandi.background.MyService

/**
 * The Main Activity of the application.
 * The application uses a single activity with a lot of fragments and a navigation panel between them.
 * currently only one fragment.
 */
class MainActivity : AppCompatActivity() {
    /**
     * On create - almost created single time. except for oriantation changes.
     * Wen activity created - make the notification channel ( can be called multiple time, if the channel already created, it does nothing)
     * Also set the tool bar
     * Also follow system night mode
     * Should move the code to app code. needto check it out. - 30/3/2022
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }

    /**
     * Creating notification channel, self explanitory
     * Low Improtance Notifications!!!
     */
    private fun createNotificationChannel()
    {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(
                CHANNEL_ID_1,
                CHANNEL_NAME_1,
                NotificationManager.IMPORTANCE_LOW
            )
        channel.description = CHANNEL_DESCRIPTION_1
        mNotificationManager.createNotificationChannel(channel)
    }
}