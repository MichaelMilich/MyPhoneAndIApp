package millich.michael.myphoneandi.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import millich.michael.myphoneandi.onboarding.screens.FirstScreen
import millich.michael.myphoneandi.onboarding.screens.SecondScreen
import millich.michael.myphoneandi.onboarding.screens.ThirdScreen

private const val NUM_TABS = 3

/**
 * The adapter for viewPager ( just like in RecycleView)
 * Is very simple, we have 3 screens. return the relevant screen when it is called
 * All the screens share the same viewModel pointer - so the adapter that calls the screens should also have the pointer.
 */
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
            1 -> return SecondScreen()
            2 -> return ThirdScreen()
        }
        return FirstScreen() // if the position is 0
    }

}