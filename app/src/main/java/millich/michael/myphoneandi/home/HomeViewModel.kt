package millich.michael.myphoneandi.home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import millich.michael.myphoneandi.*
import millich.michael.myphoneandi.background.MyService
import millich.michael.myphoneandi.database.UnlockDatabaseDAO
import millich.michael.myphoneandi.database.UnlockEvent
import java.util.*

/**
 * Currently the main viewModel for my application.
 * It is responsible for starting the Service and comunicating with it.
 * It is also responsible for providing the livedata from the database for the databinding.
 */
class HomeViewModel(val database: UnlockDatabaseDAO, application: Application) : AndroidViewModel(application) {
    companion object{
        val TAG = "HomeViewModel"
    }
    private val appContext: Context get() {return  getApplication<Application>().applicationContext}
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
    val lastUnlockTime : LiveData<String> = Transformations.map( _lastUnlock) { user ->
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

    /**
     * On starting the ViewModel - start the service.
     */
    init {
        val _intent = Intent(appContext, MyService::class.java)
        _intent.action = START_MY_SERVICE
        Log.i(TAG, "calling to start MyService ")
        appContext.startForegroundService(_intent)
    }

    fun isAfter12Am() :Boolean { return Calendar.getInstance().timeInMillis>getToday12AmInMilli()}
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
            database.getAllUnlocksBetweenTwoTimes(getCurrentDateInMilli(),getToday12AmInMilli())
        }
    }
    fun printValues(){
        _unlockEvents12H.value?.let {
            for ( event in it)
            {
                Log.i(TAG, "event in ${formatDateFromMillisecondsLong(event.eventTime)}")
            }
        }
    }


}