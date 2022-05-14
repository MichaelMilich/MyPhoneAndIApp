package millich.michael.myphoneandi.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
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
            viewModel.isPermissionGiven.value =true
            viewModel.screenNumber.value=2
        }



        return binding.root
    }

}