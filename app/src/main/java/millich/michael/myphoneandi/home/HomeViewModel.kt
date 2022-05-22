package millich.michael.myphoneandi.home

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
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
    init {
        viewModelScope.launch { // check if the database is empty, if it is insert at least one unlock.
            //Currently i am using a pre-populated table so this code doesn't get used.
            if (database.getTableCount()==0)
            {
                // this check doesn't work. i need to somehow have the application start with at least one unlock.
                database.Insert(UnlockEvent())
            }
        }
    }

    // much needed unlock count for the UI
    private val _unlockCount =  database.getTodayUnlocksCountAfterTime(getCurrentDateInMilli())
    val unlockCount : LiveData<Int>
        get() {
            return  _unlockCount
        }
    // Keeping the last unlock in the memory of the application. might be useful.
    // I also show when we had the last unlock in lastunlockTime.
    private val _lastUnlock = database.getLastUnlockLiveData()
    val lastUnlock : LiveData<UnlockEvent>
        get() {
            return _lastUnlock
        }
    val lastUnlockTime : LiveData<String> = Transformations.map( _lastUnlock , { user -> formatDateFromMillisecondsLong(user.eventTime)})

    //Simple check - are we before or after 12 AM today.
    private val _isAfter12Am : MutableLiveData<Boolean> = MutableLiveData<Boolean>().also { it.value=
        Calendar.getInstance().timeInMillis>getToday12AmInMilli() }
    val isAfter12Am : MutableLiveData<Boolean>
        get() {
            return _isAfter12Am
        }

    //The unlockevent list to be provided to the clockView - changes if we are after 12 AM
    private val _unlockEvents12H=if(isAfter12Am.value!!){ database.getAllUnlcoksFromTime(getToday12AmInMilli()) }
    else{ database.getAllUnlcoksFromTime(getCurrentDateInMilli()) }

    val unlockEvents12H : LiveData<List<UnlockEvent>>
        get() {
            return  _unlockEvents12H
        }

    // The unlock list to be provided to the recycleView
    private val _unlockEvents24H = database.getAllUnlcoksFromTime(getCurrentDateInMilli())
    val unlockEvents24H : LiveData<List<UnlockEvent>>
        get() {
            return  _unlockEvents24H
        }


    /**
     * On starting the ViewModel - start the service.
     */
    init {
        val _intent = Intent(getApplication<Application>().applicationContext, MyService::class.java)
        _intent.action = START_MY_SERVICE
        getApplication<Application>().applicationContext.startForegroundService(_intent)
    }




}