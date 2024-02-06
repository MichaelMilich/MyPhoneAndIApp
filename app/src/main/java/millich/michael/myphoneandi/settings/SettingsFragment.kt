package millich.michael.myphoneandi.settings

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.background.MyService
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.START_MY_SERVICE
import millich.michael.myphoneandi.utils.STOP_MY_SERVICE

/**
 * The settings fragment. currently has only 2 settings:
 * 1 - the service runs in the background ( ON/OFF)
 * 2- the service is in whitelisted and is not optimized for the battery [lets the service run indefinitely] ( ON/OFF)
 * The code for the fragment uses a small viewModel for readability purposes, but in the future we can migrate all of its functions to the fragment itself.
 * The code further uses a listener to listen when a shared preference was changed - there the main function is placed and from it all listener functions are called
 */
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        const val REQUEST_CODE_POST_NOTIFICATIONS = 101
        const val TAG = "SettingsFragment"
    }

    private val viewModel: SettingsViewModel by viewModels()// small viewModel
    // The main listener
    val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener{
                sharedPreferences, key -> kotlin.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (key == resources.getString(R.string.show_notification_key))
                    onShowNotificationClicked(sharedPreferences,key)
            }
            when(key) {
                resources.getString(R.string.background_service_run) -> onServicePreferenceClicked(sharedPreferences,key) // if we are talking about service setting deal with it in the function
                resources.getString(R.string.background_service_run_battery_optimization) -> onBatteryOptimizationPreferenceClicked(sharedPreferences,key) // if we are talking about battery setting deal with it in the function
                resources.getString(R.string.usage_stats_permissions_key) -> onUsageStatsPreferenceClicked(sharedPreferences,key)
            }
        } }

    /**
     * The main on create function.
     * a must have.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        checkPermissionsUI()
    }

    private fun checkPermissionsUI() {
        val batteryPref  = findPreference<SwitchPreferenceCompat>(resources.getString(R.string.background_service_run_battery_optimization))
        val batteryPrefActualValue = viewModel.isIgnoringBatteryOptimizationsGiven()
        batteryPref?.isChecked =batteryPrefActualValue

        val notificationPref = findPreference<SwitchPreferenceCompat>(resources.getString(R.string.show_notification_key))
        val notifPrefActualValue = viewModel.isNotificationPermissionGiven()
        notificationPref?.isChecked =notifPrefActualValue

        val userStatsPref = findPreference<SwitchPreferenceCompat>(resources.getString(R.string.usage_stats_permissions_key))
        val userStatsPrefActualValue = viewModel.isUsageStatsPermissionGiven()
        MLog.d(TAG, "viewmodel permissions permissions? = ${viewModel.isUsageStatsPermissionGiven()}")
        MLog.d(TAG, "does the fragment think we have permissions? = $userStatsPrefActualValue")
        userStatsPref?.isChecked =userStatsPrefActualValue
    }

    /**
     * For the listener to function correctly, we have to register it on every onResume and unregister on every onPause
     */
    override fun onResume() {
        super.onResume()
        checkPermissionsUI()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * For the listener to function correctly, we have to register it on every onResume and unregister on every onPause
     */
    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)
    }

    /**
     * The activityResultLauncher that will wait for the results of the Intent used to open the settings page
     * The handler that checks the results after going to the settings activity and changes the UI to match the results
     */
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val batteryPref  = findPreference<SwitchPreferenceCompat>(resources.getString(R.string.background_service_run_battery_optimization))
        val batteryPrefActualValue = viewModel.isIgnoringBatteryOptimizationsGiven()
        batteryPref?.isChecked =batteryPrefActualValue
        viewModel.writeBooleanPref(resources.getString(R.string.background_service_run_battery_optimization),batteryPrefActualValue)
    }

    /**
     * Start the Intent and wait for result
     */
    private fun openPowerSettings(){
        //setting up the intent for the battery optimization
        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        // launching the intent with a listener
        getResult.launch(intent)
    }

    /**
     * The function that handles the change of the battery optimization preference.
     * We have two booleans - the first is the sharedPref value (that the application thinks) and the other is the actual value that the systems gives.
     * This function deals and changes the preference to match the actual value the system gives when there is a difference.
     * The change occurs not here but in the getResult that listens to the intent that is opened.
     */
    private fun onBatteryOptimizationPreferenceClicked(sharedPreferences : SharedPreferences, key: String){
        val value = sharedPreferences.getBoolean(key, false)
        val actualValue = viewModel.isIgnoringBatteryOptimizationsGiven()
        if (value && !actualValue) {
            openPowerSettings()
        }
        if (!value && actualValue) {
            openPowerSettings()
        }

    }

    /**
     * The function that deals with the shared pref of the service.
     * It starts and stops the service according to the value of the sharedPref.
     */
    private fun onServicePreferenceClicked(sharedPreferences : SharedPreferences, key: String){
        val value = sharedPreferences.getBoolean(key, false)
        if (value){
            val _intent = Intent(requireContext().applicationContext, MyService::class.java)
            _intent.action = START_MY_SERVICE
            requireContext().applicationContext.startForegroundService(_intent)
        }else{
            val _intent = Intent(requireContext().applicationContext, MyService::class.java)
            _intent.action = STOP_MY_SERVICE
            requireContext().applicationContext.stopService(_intent)
        }
    }

    /**
     * This function is for API 33 and above.
     * Checks if the notification permission is given and compares it to what the app thinks about it.
     * The viewModel checks if the system permits the notifications and compares it to what the application think.
     * If the user wants to disable the notifications - sends the user to the page
     * Otherwise, asks for the permissions.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onShowNotificationClicked(sharedPreferences : SharedPreferences, key: String) {
        val value = sharedPreferences.getBoolean(key, false)
        var actualValue = viewModel.isNotificationPermissionGiven()
        if (actualValue && !value){
            MLog.i(TAG,"user wants to delete the notification permissions")
            Toast.makeText(requireContext(),"The app can't disable this by itself, sending you to appropriate page to disable ",Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName,null)
            intent.setData(uri)
            startActivity(intent)
            actualValue = viewModel.isNotificationPermissionGiven()
            viewModel.writeBooleanPref(resources.getString(R.string.show_notification_key), actualValue)
        }
        if (!actualValue && value) {
            MLog.i(TAG,"user wants to give the notification permissions")
            // Request permission
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
        }
    }

    private fun onUsageStatsPreferenceClicked(sharedPreferences : SharedPreferences, key: String){
        val value = sharedPreferences.getBoolean(key, false)
        var actualValue = viewModel. isUsageStatsPermissionGiven()
        if (actualValue != value){
            MLog.i(TAG,"user wants to change the usage status permissions")
            Toast.makeText(requireContext(),"The app can't change this permission itself, sending you to the relevant page ",Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName,null)
            intent.setData(uri)
            startActivity(intent)
            actualValue = viewModel.isUsageStatsPermissionGiven()
            viewModel.writeBooleanPref(resources.getString(R.string.usage_stats_permissions_key), actualValue)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            val actualValue = viewModel.isNotificationPermissionGiven()
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted, show some message or take action
                viewModel.writeBooleanPref(resources.getString(R.string.show_notification_key), actualValue)
                MLog.i(TAG,"CAN Show Notification")
            } else {
                // Permission denied, show some message to the user
                viewModel.writeBooleanPref(resources.getString(R.string.show_notification_key), actualValue)
                MLog.i(TAG,"CAN NOT Show Notification")
            }
        }
    }




}