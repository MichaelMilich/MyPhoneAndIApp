package millich.michael.myphoneandi.settings

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.START_MY_SERVICE
import millich.michael.myphoneandi.STOP_MY_SERVICE
import millich.michael.myphoneandi.background.MyService

class SettingsFragment : PreferenceFragmentCompat() {
    val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener{
                sharedPreferences, key -> kotlin.run {
            if (key == resources.getString(R.string.background_service_run)) {
                val value = sharedPreferences.getBoolean(key, false)
                Log.i("TAG", "Preference value was updated to: $value")
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
            if (key == resources.getString(R.string.background_service_run_battery_optimization)) {
                Snackbar.make(requireView(),"Didn't Implement this yet LOL ",Snackbar.LENGTH_SHORT).show()
                }
        } }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)
    }




}