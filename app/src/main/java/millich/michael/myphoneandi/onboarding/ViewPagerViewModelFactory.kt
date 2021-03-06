package millich.michael.myphoneandi.onboarding

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewPagerViewModelFactory(private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewPagerViewModel::class.java)) {
            return ViewPagerViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewPagerViewModel class")
    }
}