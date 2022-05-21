package millich.michael.myphoneandi.onboarding

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.databinding.FragmentHomeBinding
import millich.michael.myphoneandi.databinding.FragmentViewPagerBinding
import millich.michael.myphoneandi.home.HomeViewModel
import millich.michael.myphoneandi.onboarding.screens.FirstScreen
import millich.michael.myphoneandi.onboarding.screens.SecondScreen
import millich.michael.myphoneandi.onboarding.screens.ThirdScreen

/**
 * The main fragment for the onBoarding process.
 * Contains a viewPager2 that holds all the fragments.
 */
class ViewPagerFragment : Fragment() {

    companion object {
        fun newInstance() = ViewPagerFragment()
    }

    private lateinit var  viewModel: ViewPagerViewModel

    /**
     * Create the view, the viewModel , the viewPageAdapter, Add observers on the viewModel liveData
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentViewPagerBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_view_pager,
                container,
                false)
        //Create the VieModel through the Factory
        val factory = ViewPagerViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(ViewPagerViewModel::class.java)
        binding.viewModel = viewModel

        //Create the Adapter
        val adapter = ViewPagerAdapter(viewModel,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter=adapter
        binding.viewPager.isUserInputEnabled =false
        viewModel.screenNumber.value=0

        //The navigation is done through the LiveData and observer.
        // When we click on Next ( in the 1st and 2nd screens) we change the viewModel's screen number Livedata
        //Causing the observer to change the screen on the viewPager
        viewModel.screenNumber.observe(viewLifecycleOwner, Observer {
                screenNumber -> binding.viewPager.setCurrentItem(screenNumber,true)
        })

        //Another navigation feature, if the viewModel's battery permission is true - go to the last screen number
        viewModel.isPermissionGiven.observe(viewLifecycleOwner, Observer {
            bool -> if (bool) binding.viewPager.setCurrentItem(2,true)
        })

        // Add Tabs that show the user how many screens are left.
        TabLayoutMediator(binding.tabLayout,binding.viewPager) { tab, position -> tab.text =""}.attach()

        return binding.root
    }

    /**
     * The ViewPager Fragment is the starting point of the navigation.
     * But, the second we load it, we need to check if we already did the onBoarding.
     * If we did, we have to go to the homeFragment.
     * That is why - as soon as we went through onCreateView and into onViewCreated we check through the viewmodel if we need to pass to the homeFragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isOnBoardingFinished())
            findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar!!.show()
    }

}