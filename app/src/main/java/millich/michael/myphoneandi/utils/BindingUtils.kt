package millich.michael.myphoneandi.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.database.screenevents.ScreenEvent
import millich.michael.myphoneandi.home.ClockView
import millich.michael.myphoneandi.home.HomeViewModel


@BindingAdapter("unlockIdText")
fun TextView.setUnlockIdText(item : ScreenEvent){
    "unlock number ${item.eventId}".also { text=it }
}

@BindingAdapter("unlockTimeText")
fun TextView.setUnlockTimeText(item: ScreenEvent){
    text = formatDateFromMillisecondsLong(item.eventTime)
}

@BindingAdapter("unlockTimeTag")
fun ImageView.setTagImage(item: ScreenEvent){
    setImageResource(R.drawable.ic_dot)
}


@BindingAdapter("ViewModel")
fun ClockView.setViewModel(viewModel: HomeViewModel){
    binding.viewModelClock=viewModel
    onBind()
}

@BindingAdapter("onBoardingText")
fun TextView.setOnBoardingText(item : Boolean){
    text = if (item)
        resources.getText(R.string.on_boarding_done_text_permission_given)
    else
        resources.getText(R.string.on_boarding_done_text_permission_denied)

}
@BindingAdapter("showDate")
fun TextView.setDateText(item: ScreenEvent){
    text = formatSimpleDate()
}