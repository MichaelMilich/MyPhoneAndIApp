package millich.michael.myphoneandi.home

import android.util.Log
import androidx.lifecycle.*

import dagger.hilt.android.lifecycle.HiltViewModel
import millich.michael.myphoneandi.database.UnlockDatabaseDAO
import millich.michael.myphoneandi.database.UnlockEvent
import millich.michael.myphoneandi.utils.MLog
import millich.michael.myphoneandi.utils.formatDateFromMillisecondsLong
import millich.michael.myphoneandi.utils.formatSimpleDate
import millich.michael.myphoneandi.utils.formatTimeWords
import millich.michael.myphoneandi.utils.getCurrentDateInMilli
import millich.michael.myphoneandi.utils.getToday12AmInMilli
import java.util.*
import javax.inject.Inject

/**
 * Currently the main viewModel for my application.
 * It is responsible for starting the Service and comunicating with it.
 * It is also responsible for providing the livedata from the database for the databinding.
 */

@HiltViewModel
class HomeViewModel @Inject constructor(val database: UnlockDatabaseDAO) : ViewModel() {
    companion object{
        val TAG = "HomeViewModel"
    }
    /*init {
        viewModelScope.launch { // check if the database is empty, if it is insert at least one unlock.
            //Currently i am using a pre-populated table so this code doesn't get used.
            if (database.getTableCount()==0)
            {
                // this check doesn't work. i need to somehow have the application start with at least one unlock.
                database.Insert(UnlockEvent())
            }
        }
    }*/

    // much needed unlock count for the UI
    private var _unlockCount =  database.getTodayUnlocksCountAfterTime(getCurrentDateInMilli())
    val unlockCount : LiveData<Int>
        get() {
            return  _unlockCount
        }
    // Keeping the last unlock in the memory of the application. might be useful.
    // I also show when we had the last unlock in lastunlockTime.
    private val _lastUnlock = database.getLastUnlockLiveData()
    val lastUnlockTime : LiveData<String> = _lastUnlock.map { user ->
        formatDateFromMillisecondsLong(
            user.eventTime
        )
    }
    val dateText : MutableLiveData<String> = MutableLiveData<String>(formatSimpleDate())
    val dateTextWords : MutableLiveData<String> = MutableLiveData<String>(formatTimeWords())

    //The unlockevent list to be provided to the clockView - changes if we are after 12 AM
    private var _unlockEvents12H= _unlockEvents12HRefresh()

    val unlockEvents12H : LiveData<List<UnlockEvent>>
        get() {
            return  _unlockEvents12H
        }



    fun isAfter12Am() :Boolean { return Calendar.getInstance().timeInMillis> getToday12AmInMilli()
    }
    private var lastCheckIsAfter12Am = isAfter12Am()
    fun shouldRefresh() : Boolean{
        if (lastCheckIsAfter12Am!=isAfter12Am()){
            lastCheckIsAfter12Am = isAfter12Am()
            return true
        }
        return false
    }

    fun refresh(){
        _unlockEvents12H=_unlockEvents12HRefresh()
    }
    fun _unlockEvents12HRefresh() : LiveData<List<UnlockEvent>>{
        return if (isAfter12Am()){
            database.getAllUnlcoksFromTime(getToday12AmInMilli())
        }
            else  {
            database.getAllUnlocksBetweenTwoTimes(getCurrentDateInMilli(), getToday12AmInMilli())
        }
    }
    fun printValues(){
        _unlockEvents12H.value?.let {
            for ( event in it)
            {
                MLog.i(TAG, "event in ${formatDateFromMillisecondsLong(event.eventTime)}")
            }
        }
    }


}