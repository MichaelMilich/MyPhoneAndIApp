package millich.michael.myphoneandi.onboarding.screens

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.databinding.FragmentSecondScreenBinding
import millich.michael.myphoneandi.onboarding.OnBoardingViewModel

/**
 * Second screen of the onBoarding.
 * Here there is the logic for the permission.
 * There are three buttons - enable, skip and show me how.
 * enable - takes the user to the settings of the phone to change the setting of the App.
 * skip - skips this process
 * show me how - should open the youtube to show how to do this.
 */
class SecondScreen() : Fragment() {
    private val viewModel : OnBoardingViewModel by activityViewModels()

    // The activityResultLauncher that will wait for the results of the Intent used to open the settings page
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        viewModel.testBatteryOptimization()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentSecondScreenBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_second_screen, container, false)
        binding.viewModel=viewModel

        //If we skip- go to the last screen
        binding.buttonSkip.setOnClickListener {
            viewModel.screenNumber.value=2
        }
        // If we go to enable the permissions - we launch the procedure of the intent.
        // The flow of the code is openPowerSettings -> getResult.OnReturnedResults -> viewModel - make sure we recieved the permission -> if yes, change LiveData.
        // ViewPagerFragment observer is triggered and sends us to the last screen with the good text
        binding.buttonPermission.setOnClickListener {
            openPowerSettings()
        }
        //This part i need to implement as well. open an intent to youtube with a specific link
        binding.buttonExample.setOnClickListener{
            viewModel.openYoutubeVideoExample()
        }
        return binding.root
    }

    private fun openPowerSettings(){
        //setting up the intent for the battery optimization
        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        // launching the intent with a listener
        getResult.launch(intent)
    }





}