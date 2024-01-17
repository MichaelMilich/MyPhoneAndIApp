package millich.michael.myphoneandi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import millich.michael.myphoneandi.background.MyService
import millich.michael.myphoneandi.databinding.ActivityMainBinding
import millich.michael.myphoneandi.home.HomeViewModel
import millich.michael.myphoneandi.utils.CHANNEL_DESCRIPTION_1
import millich.michael.myphoneandi.utils.CHANNEL_ID_1
import millich.michael.myphoneandi.utils.CHANNEL_NAME_1
import millich.michael.myphoneandi.utils.START_MY_SERVICE

/**
 * The Main Activity of the application.
 * The application uses a single activity with a lot of fragments and a navigation panel between them.
 * currently only one fragment.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "MainActivity"
    }

    private lateinit var drawerLayout: DrawerLayout
    /**
     * On create - almost created single time. except for oriantation changes.
     * Wen activity created - make the notification channel ( can be called multiple time, if the channel already created, it does nothing)
     * Also set the tool bar
     * Also follow system night mode
     * Should move the code to app code. needto check it out. - 30/3/2022
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        createNotificationChannel()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        drawerLayout = binding.drawerLayout
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setSupportActionBar(findViewById(R.id.my_toolbar))
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        lifecycleScope.launch(Dispatchers.IO) {
            startMyService()
        }
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

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.title){
            resources.getString(R.string.refresh) -> {
                Snackbar.make(drawerLayout, "Clickity clack", Snackbar.LENGTH_SHORT).show()
                val fragment= supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                val msg = fragment.toString() ?: "null "
                Log.i("Test","fragment is $msg")
                fragment?.let {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.detach(it)
                    transaction.attach(it)
                    transaction.commit()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * On starting the Activity - start the service. currently calling it from Dispatchers.IO
     */
    private fun startMyService() {
        val _intent = Intent(applicationContext, MyService::class.java)
        _intent.action = START_MY_SERVICE
        Log.i(TAG, "calling to start MyService ")
        applicationContext.startForegroundService(_intent)
    }

}