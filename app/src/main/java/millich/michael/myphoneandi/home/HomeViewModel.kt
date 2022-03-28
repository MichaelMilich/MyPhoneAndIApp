package millich.michael.myphoneandi.home

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import millich.michael.myphoneandi.*
import millich.michael.myphoneandi.background.MyService
import millich.michael.myphoneandi.database.UnlockDatabaseDAO
import millich.michael.myphoneandi.database.UnlockEvent
import java.util.*


class HomeViewModel(val database: UnlockDatabaseDAO, application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    init {
        viewModelScope.launch {
            if (database.getTableCount()==0)
            {
                // this check doesn't work. i need to somehow have the application start with at least one unlock.
                database.Insert(UnlockEvent())
            }
        }
    }

    private val _buttonsVisible= MutableLiveData<Boolean>()
    val buttonVisible : LiveData<Boolean>
        get() = _buttonsVisible

    private val _unlockCount =  database.getTodayUnlocksCountAfterTime(getCurrentDateInMilli())
    val unlockCount : LiveData<Int>
        get() {
            return  _unlockCount
        }
    private val _lastUnlock = database.getLastUnlockLiveData()
    val lastUnlock : LiveData<UnlockEvent>
        get() {
            return _lastUnlock
        }
    val lastUnlockTime : LiveData<String> = Transformations.map( _lastUnlock , { user -> formatDateFromMillisecondsLong(user.eventTime)})
    private val _isAfter12Am : MutableLiveData<Boolean> = MutableLiveData<Boolean>().also { it.value=
        Calendar.getInstance().timeInMillis>getToday12AmInMilli() }
    val isAfter12Am : MutableLiveData<Boolean>
        get() {
            return _isAfter12Am
        }

    private val _unlockEvents=if(isAfter12Am.value!!){ database.getAllUnlcoksFromTime(getToday12AmInMilli()) }
    else{ database.getAllUnlcoksFromTime(getCurrentDateInMilli()) }

    val unlockEvents : LiveData<List<UnlockEvent>>
        get() {
            return  _unlockEvents
        }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i("HomeViewModel","Bounded to service")
            val binder = service as MyService.LocalBinder
            //mService = binder.getService()
            _buttonsVisible.value=true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.i("HomeViewModel","Disconnected from service")
            _buttonsVisible.value =false
        }
    }

    init {
        /*viewModelScope.launch {
            testEvents=if(isAfter12Am.value!!){ database.getAllUnlcoksFromTimeNoLiveData(getToday12AmInMilli()) }
            else{ database.getAllUnlcoksFromTimeNoLiveData(getCurrentDateInMilli()) }
        }*/
        _buttonsVisible.value=false
        start()
    }
    fun start(){
        _buttonsVisible.value=true
        val _intent = Intent(context, MyService::class.java)
        _intent.action = START_MY_SERVICE
        val pendingIntent = PendingIntent.getService(context, START_MY_SERVICE_INT,_intent,PendingIntent.FLAG_IMMUTABLE)
        context.startForegroundService(_intent)
        //Intent(context,MyService::class.java).also { intent -> context.bindService(intent,connection,0) }
    }
    fun stop(){
        context.unbindService(connection)
        _buttonsVisible.value =false
        val _intent = Intent(context, MyService::class.java)
        _intent.action = STOP_MY_SERVICE
        val pendingIntent = PendingIntent.getService(context, STOP_MY_SERVICE_INT,_intent,PendingIntent.FLAG_IMMUTABLE)
        context.stopService(_intent)
    }


}