package millich.michael.myphoneandi.home.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import millich.michael.myphoneandi.onboarding.ViewPagerViewModel
import millich.michael.myphoneandi.onboarding.screens.FirstScreen
import millich.michael.myphoneandi.onboarding.screens.SecondScreen
import millich.michael.myphoneandi.onboarding.screens.ThirdScreen

private const val NUM_TABS = 3

class HomeViewPagerAdapter(
    val viewModel : ViewPagerViewModel,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {


    override fun getItemCount(): Int {
        return NUM_TABS // I want to be able to show 3 screens at most each time
    }

    /**
     * Whenever we call a new fragment, The HomeViewPagerViewModel will show a new 'homefragment' with a viewmodel based on different time period.
     * The HomeViewPagerViewModel has to have an function to transfer this time period to these new 'homefragment' and thier respective viewModels.
     */
    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return SecondScreen()
            2 -> return ThirdScreen()
        }
        return FirstScreen() // if the position is 0
    }

}