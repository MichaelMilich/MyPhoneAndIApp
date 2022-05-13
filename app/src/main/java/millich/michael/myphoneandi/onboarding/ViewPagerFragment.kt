package millich.michael.myphoneandi.onboarding

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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

    private lateinit var viewModel: ViewPagerViewModel

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

        val factory = ViewPagerViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(ViewPagerViewModel::class.java)
        binding.viewModel = viewModel

        val fragmentList = arrayListOf<Fragment>(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter=adapter

        return binding.root
    }


}