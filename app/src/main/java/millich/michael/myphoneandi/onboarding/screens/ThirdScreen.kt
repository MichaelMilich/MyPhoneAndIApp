package millich.michael.myphoneandi.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.databinding.FragmentThirdScreenBinding
import millich.michael.myphoneandi.onboarding.ViewPagerViewModel

/**
 * Also, very basic third screen.
 * On the last screen we just have an ok button that takes us to the homefragment.
 * The screen is reactive to the desicions of the user in the second screen,
 * It changes the text according to the desicions.
 * The change apears in the onResume of the screen. A little bit stupid way to implement this, but it works.
 */
class ThirdScreen() : Fragment() {
    private lateinit var textView: TextView // save the link to this view for later
    private val viewModel : ViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentThirdScreenBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_third_screen, container, false)
        binding.button.setOnClickListener{
            viewModel.writeOnBoardingFinished()
            binding.root.findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
        }
        binding.viewModel=viewModel
        textView=binding.description3

        return binding.root
    }

    /**
     * It changes the text according to the desicions.
     * The change apears in the onResume of the screen. A little bit stupid way to implement this, but it works.
     */
    override fun onResume() {
        if (viewModel.isPermissionGiven.value==true)
        {
            textView.text=resources.getText(R.string.on_boarding_done_text_permission_given)
        }
        else
            textView.text=resources.getText(R.string.on_boarding_done_text_permission_denied)
        super.onResume()
    }

}