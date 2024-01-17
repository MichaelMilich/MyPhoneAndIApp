package millich.michael.myphoneandi.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.databinding.FragmentFirstScreenBinding
import millich.michael.myphoneandi.onboarding.OnBoardingViewModel

/**
 * First Screen in OnBoarding.
 * very short and Self Explanitory, no Comments
 */
class FirstScreen() : Fragment() {
private val viewModel : OnBoardingViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentFirstScreenBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_first_screen,container,false)
        binding.buttonNext.setOnClickListener {
            viewModel.screenNumber.value=1
        }
        binding.viewModel=viewModel



        return binding.root
    }

}