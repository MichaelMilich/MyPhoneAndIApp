package millich.michael.myphoneandi.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import millich.michael.myphoneandi.R
import millich.michael.myphoneandi.database.UnlockEvent
import millich.michael.myphoneandi.home.ClockView
import millich.michael.myphoneandi.home.HomeViewModel


@BindingAdapter("unlockIdText")
fun TextView.setUnlockIdText(item : UnlockEvent){
    "unlock number ${item.eventId}".also { text=it }
}

@BindingAdapter("unlockTimeText")
fun TextView.setUnlockTimeText(item: UnlockEvent){
    text = formatDateFromMillisecondsLong(item.eventTime)
}

@BindingAdapter("unlockTimeTag")
fun ImageView.setTagImage(item: UnlockEvent){
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
fun TextView.setDateText(item: UnlockEvent){
    text = formatSimpleDate()
}