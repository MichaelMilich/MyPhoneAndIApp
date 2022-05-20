package millich.michael.myphoneandi.onboarding

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class ViewPagerFragment : Fragment() {

    companion object {
        fun newInstance() = ViewPagerFragment()
    }

    private lateinit var  viewModel: ViewPagerViewModel

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

        val factory = ViewPagerViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(ViewPagerViewModel::class.java)
        binding.viewModel = viewModel


        val adapter = ViewPagerAdapter(viewModel,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter=adapter
        viewModel.screenNumber.value=0
        viewModel.screenNumber.observe(viewLifecycleOwner, Observer {
                screenNumber -> binding.viewPager.setCurrentItem(screenNumber,true)
        })
        binding.viewPager.isUserInputEnabled =false
        viewModel.isPermissionGiven.observe(viewLifecycleOwner, Observer {
            bool -> if (bool) binding.viewPager.setCurrentItem(2,true)
        })

        TabLayoutMediator(binding.tabLayout,binding.viewPager) { tab, position -> tab.text =""}.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isOnBoardingFinished())
            findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
    }

}