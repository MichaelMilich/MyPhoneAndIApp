// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package millich.michael.myphoneandi.utils

import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.LiveData
import millich.michael.myphoneandi.database.ScreenEvent
import java.text.SimpleDateFormat
import java.util.*


const val CHANNEL_ID_1 ="MISHA_CHANNEL_ID_1"

const val CHANNEL_NAME_1 ="MISHA_CHANNEL_NAME_1"

const val CHANNEL_DESCRIPTION_1 ="Channel for my application to show live data"

const val ONGOING_NOTIFICATION_ID=1234

const val STOP_MY_SERVICE="STOP_MY_SERVICE"
const val STOP_MY_SERVICE_INT=10

const val START_MY_SERVICE="START_MY_SERVICE"
const val START_MY_SERVICE_INT=11

const val SERVICE_START_NOTIFICATION_LOOP="SERVICE_START_NOTIFICATION_LOOP"
const val SERVICE_START_NOTIFICATION_LOOP_INT=12

const val SERVICE_STOP_NOTIFICATION_LOOP="SERVICE_STOP_NOTIFICATION_LOOP"
const val SERVICE_STOP_NOTIFICATION_LOOP_INT=13

fun formatDateFromMillisecondsLong( long: Long) :String
{
    val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
    simpleDateFormat.timeZone = TimeZone.getDefault()
    val date = Date(long)
    return simpleDateFormat.format(date)
}

fun formatDuration(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
//    val secs = seconds % 60

    return "$hours Hours,$minutes Minutes "
}


fun formatSimpleDate() :String
{
    val calendar = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    simpleDateFormat.timeZone = TimeZone.getDefault()
    return simpleDateFormat.format(calendar.time)
}
fun formatTimeWords() :String {
    val now =Calendar.getInstance().timeInMillis
    val afternoon= getToday12AmInMilli()
    if (now > afternoon)
        return "12 - 24 (PM)"
    else
        return " 0 - 12 (AM)"
}

fun getCurrentTimeInString(): String
{
    val calendar = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat("yyyy_MM_dd_hh:mm:ss")
    simpleDateFormat.timeZone = TimeZone.getDefault()
    return simpleDateFormat.format(calendar.time)
}
fun getCurrentTimeInStringUnderScore(): String
{
    val calendar = Calendar.getInstance()
    val simpleDateFormat = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")
    simpleDateFormat.timeZone = TimeZone.getDefault()
    return simpleDateFormat.format(calendar.time)
}
fun getCurrentDateInMilli() : Long{
    val today = Calendar.getInstance()
    today.set(Calendar.MILLISECOND,0)
    today.set(Calendar.SECOND,0)
    today.set(Calendar.MINUTE,0)
    today.set(Calendar.HOUR_OF_DAY,0)
    return today.timeInMillis
}
fun getToday12AmInMilli() : Long{
    val today = Calendar.getInstance()
    today.set(Calendar.MILLISECOND,0)
    today.set(Calendar.SECOND,0)
    today.set(Calendar.MINUTE,0)
    today.set(Calendar.HOUR_OF_DAY,12)
    return today.timeInMillis
}
fun getTodayEndInMilli() : Long{
    val today = Calendar.getInstance()
    today.set(Calendar.MILLISECOND,99)
    today.set(Calendar.SECOND,59)
    today.set(Calendar.MINUTE,59)
    today.set(Calendar.HOUR_OF_DAY,23)
    return (today.timeInMillis+1)
}

/**
 * This function works only for time tags that are within this date.
 * If we set timeTag to be larger than this dates end, the angle is 0.
 * otherwise this function calculates the relative angle the visual tag should be placed at in respect to the 12 am and the 24pm analog clocks
 */
fun calculateAngle(timeTag :Long) : Float {
    val dayEnd = getTodayEndInMilli()
    if (timeTag > dayEnd)
        return 0f

    val _12am = getToday12AmInMilli()
    val _0am = getCurrentDateInMilli()
    val _12hoursInMilli = (_12am - _0am).toFloat()
    var timeDelta =(timeTag - _12am).toFloat()
    var angle = 0f
    if (timeDelta > 0) {
        angle = ((timeDelta/_12hoursInMilli)*360)
    }
    else{
        timeDelta = (timeTag - _0am).toFloat()
        angle = ((timeDelta/_12hoursInMilli)*360)
    }
    return angle
}

inline fun View.afterMeasured(crossinline f: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredHeight > 0 && measuredWidth > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

/*fun refreshFragment(context: Context? ){
    context?.let {
        val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
        fragmentManager?.let {
            val currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment)
            currentFragment?.let {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.detach(it)
                fragmentTransaction.attach(it)
                fragmentTransaction.commit()
            }
        }
    }
}*/

fun setScreenEventId(screenEvents: List<ScreenEvent>){
    MLog.i("ScreenOff", " calling setScreenEventId on $screenEvents")
    for (index : Int in screenEvents.indices){
        screenEvents[index].eventId = (screenEvents.size -index).toLong()
    }
}