package millich.michael.myphoneandi.settings

import android.content.*
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.utils.START_MY_SERVICE
import millich.michael.myphoneandi.utils.STOP_MY_SERVICE
import millich.michael.myphoneandi.background.MyService

/**
 * The settings fragment. currently has only 2 settings:
 * 1 - the service runs in the background ( ON/OFF)
 * 2- the service is in whitelisted and is not optimized for the battery [lets the service run indefinitely] ( ON/OFF)
 * The code for the fragment uses a small viewModel for readability purposes, but in the future we can migrate all of its functions to the fragment itself.
 * The code further uses a listener to listen when a shared preference was changed - there the main function is placed and from it all listener functions are called
 */
class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel // small viewModel
    // The main listener
    val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener{
                sharedPreferences, key -> kotlin.run {
            when(key) {
                resources.getString(R.string.background_service_run) -> onServicePreferenceClicked(sharedPreferences,key) // if we are talking about service setting deal with it in the function
                resources.getString(R.string.background_service_run_battery_optimization) -> onBatteryOptimizationPreferenceClicked(sharedPreferences,key) // if we are talking about battery setting deal with it in the function
            }
        } }

    /**
     * The main on create function.
     * a must have.
     * also creates the viewmodel.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val factory = SettingsViewModel.Factory(requireActivity().application)
        viewModel = ViewModelProvider(this,factory).get(SettingsViewModel::class.java)

        // check if permission is given for the battery optimization and set it in the start of the fragment
        val batteryPref  = findPreference<SwitchPreferenceCompat>(resources.getString(R.string.background_service_run_battery_optimization))
        val actualValue = viewModel.isPermissionGiven.value!!
        batteryPref!!.isChecked =actualValue
    }

    /**
     * For the listener to function correctly, we have to register it on every onResume and unregister on every onPause
     */
    override fun onResume() {
        super.onResume()
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
        val actualValue = viewModel.isPermissionGiven.value!!
        batteryPref!!.isChecked =actualValue
        viewModel.writeBatteryOptimizationPref(resources.getString(R.string.background_service_run_battery_optimization),actualValue)
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
    fun onBatteryOptimizationPreferenceClicked(sharedPreferences : SharedPreferences, key: String){
        val value = sharedPreferences.getBoolean(key, false)
        val actualValue = viewModel.isPermissionGiven.value!!
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
    fun onServicePreferenceClicked(sharedPreferences : SharedPreferences, key: String){
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




}