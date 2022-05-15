package millich.michael.myphoneandi.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.databinding.FragmentSecondScreenBinding
import millich.michael.myphoneandi.onboarding.ViewPagerViewModel


class SecondScreen(val viewModel: ViewPagerViewModel) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentSecondScreenBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_second_screen, container, false)
        binding.viewModel=viewModel

        binding.buttonSkip.setOnClickListener {
            viewModel.isPermissionGiven.value =false
            viewModel.screenNumber.value=2
        }
        binding.buttonPermission.setOnClickListener {
            openPowerSettings(requireContext())
            viewModel.isPermissionGiven.value =true
            viewModel.screenNumber.value=2
        }
        return binding.root
    }

    private fun openPowerSettings(context: Context){
        var intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        context.startActivity(intent)
    }

    override fun onResume() {
        testBatteryOptimization()
        super.onResume()
    }

    private fun testBatteryOptimization(){
        val intent = Intent()
        val powerManager = requireContext().applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = requireContext().applicationContext.packageName
        if (!powerManager.isIgnoringBatteryOptimizations(packageName))
        {
            Log.i("Battery" , "Not ignoring Battery Optimization")
        }
        else
        {
            Log.i("Battery" , " ignoring Battery Optimization!")
        }
    }


}