package millich.michael.myphoneandi.database.screenevents

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface ScreenEventDatabaseDAO {
    @Insert
    suspend fun Insert(event: ScreenEvent)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param event new value to write
     */
    @Update
    suspend fun update(event: ScreenEvent)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from screen_event_table WHERE eventId = :key")
    suspend fun get(key: Long): ScreenEvent

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM screen_event_table")
    suspend fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM screen_event_table WHERE event_type = :eventType ORDER BY eventId DESC")
    fun getAllUnlcoks(eventType: String = ScreenEventType.ScreenOn.value): LiveData<List<ScreenEvent>>

    /**
     * Selects and returns the latest unlock event.
     */
    @Query("SELECT * FROM screen_event_table WHERE event_type = :eventType ORDER BY eventId DESC LIMIT 1")
    suspend fun getLastUnlock(eventType: String = ScreenEventType.ScreenOn.value): ScreenEvent?

    /**
     * Selects and returns the latest screen event (unlock or screen off) event.
     */
    @Query("SELECT * FROM screen_event_table ORDER BY eventId DESC LIMIT 1")
    suspend fun getLastScreenEvent(): ScreenEvent?

    @Query("SELECT * FROM screen_event_table WHERE event_type = :eventType ORDER BY eventId DESC LIMIT 1")
    fun getLastUnlockLiveData(eventType: String = ScreenEventType.ScreenOn.value): LiveData<ScreenEvent?>

    /**
     * Selects and returns an unlock event with given eventId.
     */
    @Query("SELECT * from screen_event_table WHERE eventId = :key")
    fun getUnlockEventWithId(key: Long): LiveData<ScreenEvent>

    @Query("SELECT * from screen_event_table WHERE event_time_milli > :event AND event_type = :eventType ORDER BY eventId")
    fun getUnlocksAfterTime(event : Long ,eventType: String = ScreenEventType.ScreenOn.value) : LiveData<List<ScreenEvent>>

    @Query("SELECT COUNT(*) from  screen_event_table WHERE event_time_milli > :event AND event_type = :eventType")
    fun getTodayUnlocksCountAfterTime(event: Long,eventType: String = ScreenEventType.ScreenOn.value) : LiveData<Int>

    @Query("SELECT COUNT(*) from  screen_event_table WHERE event_type = :eventType AND event_time_milli BETWEEN :start AND :end ")
    fun getUnlocksCountBetweenTimes(start : Long, end : Long, eventType: String = ScreenEventType.ScreenOn.value) : LiveData<Int>

    @Query("SELECT COUNT(*) from  screen_event_table WHERE event_time_milli > :event AND event_type = :eventType")
    suspend fun getTodayScreenEventCountAfterTimeNoLiveData(event: Long, eventType: String = ScreenEventType.ScreenOn.value) : Int

    @Query("SELECT * FROM screen_event_table WHERE event_time_milli > :time AND event_type = :eventType ORDER BY eventId DESC")
    fun getAllUnlcoksFromTime(time : Long ,eventType: String = ScreenEventType.ScreenOn.value): LiveData<List<ScreenEvent>>

    @Query("SELECT * FROM screen_event_table WHERE event_time_milli > :time AND event_type = :eventType ORDER BY eventId DESC")
    suspend fun getAllUnlcoksFromTimeNoLiveData(time : Long , eventType: String = ScreenEventType.ScreenOn.value): List<ScreenEvent>

    @Query("SELECT COUNT(*) from  screen_event_table ")
    suspend fun getTableCount():Int

    @Query("SELECT * FROM screen_event_table WHERE event_type = :eventType AND event_time_milli BETWEEN :start AND :end ORDER BY eventId DESC ")
     fun getAllScreenEventsBetweenTwoTimes(start : Long, end : Long, eventType: String = ScreenEventType.ScreenOn.value) : LiveData<List<ScreenEvent>>

    @Query("SELECT event_time_milli FROM screen_event_table WHERE event_time_milli > :time AND event_type = :eventType ORDER BY event_time_milli ASC LIMIT 1")
    fun getTimeOfFirstUnlockFromTime(time: Long, eventType: String = ScreenEventType.ScreenOn.value): Long?

    @Query("SELECT SUM(event_time_milli) FROM screen_event_table WHERE event_time_milli > :time AND event_type = :eventType")
    fun getSumOfTimestampsFromTime(time: Long, eventType: String = ScreenEventType.ScreenOn.value) : Long?

}