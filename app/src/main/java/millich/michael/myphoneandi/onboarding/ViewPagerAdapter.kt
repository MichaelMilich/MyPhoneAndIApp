package millich.michael.myphoneandi.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import millich.michael.myphoneandi.onboarding.screens.FirstScreen
import millich.michael.myphoneandi.onboarding.screens.SecondScreen
import millich.michael.myphoneandi.onboarding.screens.ThirdScreen

private const val NUM_TABS = 3

class ViewPagerAdapter(
    val viewModel : ViewPagerViewModel,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {


    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return SecondScreen(viewModel)
            2 -> return ThirdScreen(viewModel)
        }
        return FirstScreen(viewModel) // if the position is 0
    }

}