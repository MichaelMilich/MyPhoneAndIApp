package millich.michael.myphoneandi

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import com.google.android.material.snackbar.Snackbar
import millich.michael.myphoneandi.database.UnlockEvent
import millich.michael.myphoneandi.databinding.ClockViewBinding
import millich.michael.myphoneandi.home.HomeViewModel
import kotlin.math.cos
import kotlin.math.sin

/**
 * This class is responsible to make the clock view with the tags.
 * It uses the clockView.xml layout file via binding.
 * The ClockView Recives a list of unlocks from the viewmodel (currently from a function in the fragment - can change that in the future) and places the visual tags where needed.
 * The clockView also changes the analog clock from 0 to 12 and from 12 to 24 according to the time.
 */
class ClockView : RelativeLayout {
    //Constructor that are used by the layout file.
    constructor(context: Context?) : super(context) {
        test1()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        test2()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        test3()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        test4()
    }
    val binding : ClockViewBinding= ClockViewBinding.inflate(LayoutInflater.from(context),this,true)
    private lateinit var viewModel: HomeViewModel
    private var eventViewMap : MutableMap<Long, ImageView> = mutableMapOf()
    private fun test1() {
        // my own code to check when each constructor is called
    }

    private fun test2() {
        // my own code to check when each constructor is called
    }

    private fun test3() {
        // my own code to check when each constructor is called
    }

    private fun test4() {
        // my own code to check when each constructor is called
    }
    /**
     * This function is the binding part of the cockView with the viewModel.
     * This function is called by the binding (coded in BindingUtils)
     */
    fun onBind() {
        viewModel = binding.viewModelClock!!
        if (viewModel.isAfter12Am())
            binding.analogClockView.setImageResource(R.drawable.ic_analog_clock_12_24)
//        viewModel.isAfter12Am.observe(binding.lifecycleOwner!!,androidx.lifecycle.Observer{
//            checkClock()
//        })
    }

    /**
     * update the analog clock to 0-12H or 12-24H image.
     */
    fun checkClock(){
        Log.i("Test","Are we now between 12 and 24  = ${viewModel.isAfter12Am()}")
        Snackbar.make(this,"Are we now between 12 and 24  = ${viewModel.isAfter12Am()}",Snackbar.LENGTH_SHORT).show()
        if (viewModel.isAfter12Am())
            binding.analogClockView.setImageResource(R.drawable.ic_analog_clock_12_24)
        else
            binding.analogClockView.setImageResource(R.drawable.ic_analog_clock_0_12)
    }

    /**
     * Add the visual tags on the analog clock image.
     * the function recives the unlock event list (0-12H or 12-24H) after it was changed (in the fragment code)
     * it then compares the unlockevent list with its own internal map of unlockid (key) and the visual tag - imageview (Vlaue)
     * If the list has a new key that isn't in the map - we add the key and value to the map and to the relativelayoutview with the apropriate claculations.
     * If the map has old keys that are not in the list - we remove them from the map and from the relativelayoutview.
     */
    fun createTimeTags(eventList: List<UnlockEvent>, radius: Float) {
        for (event in eventList) {
            val key = event.eventId
            if (!eventViewMap.containsKey(key)) {
                val testImageView = ImageView(context)
                testImageView.setImageResource(R.drawable.ic_dot)
                val angle1 = calculateAngle(event.eventTime)
                val angle =
                    ((90 - angle1) * 0.017453).toFloat() // 0.017453 = 1 degree to radians
                val imageParameters =
                    LayoutParams(40, 40)
                imageParameters.addRule(CENTER_IN_PARENT, TRUE)
                testImageView.layoutParams = imageParameters
                testImageView.translationX = radius * cos(angle)
                testImageView.translationY = -radius * sin(angle)
                testImageView.rotation = angle1
                eventViewMap[key] = testImageView
                binding.relativeLayout.addView(testImageView)
            }
        }

        //This part checks if there are leftover imageviews in the map and reletivelayout that shouldn't be in the clock view
        // The leftovers can be in the relativelayout if we moved to another 12 hours timeframe
        for((key,value) in eventViewMap)
        {
            var bool =true
            for (event in eventList)
            {
                if(key == event.eventId) {
                    bool = false
                    break
                }
            }
            if(bool)
            {
                binding.relativeLayout.removeView(value)
                eventViewMap.remove(key)
            }
        }
    }

}