package millich.michael.myphoneandi.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewPagerViewModelFactory(
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewPagerViewModel::class.java)) {
            return ViewPagerViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewPagerViewModel class")
    }
}