package millich.michael.myphoneandi.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface UnlockDatabaseDAO {
    @Insert
    suspend fun Insert(event: UnlockEvent)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param event new value to write
     */
    @Update
    suspend fun update(event: UnlockEvent)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from unlock_event_table WHERE eventId = :key")
    suspend fun get(key: Long): UnlockEvent

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM unlock_event_table")
    suspend fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM unlock_event_table ORDER BY eventId DESC")
    fun getAllUnlcoks(): LiveData<List<UnlockEvent>>

    /**
     * Selects and returns the latest unlock event.
     */
    @Query("SELECT * FROM unlock_event_table ORDER BY eventId DESC LIMIT 1")
    suspend fun getLastUnlock(): UnlockEvent?

    @Query("SELECT * FROM unlock_event_table ORDER BY eventId DESC LIMIT 1")
    fun getLastUnlockLiveData(): LiveData<UnlockEvent>

    /**
     * Selects and returns an unlock event with given eventId.
     */
    @Query("SELECT * from unlock_event_table WHERE eventId = :key")
    fun getUnlockEventWithId(key: Long): LiveData<UnlockEvent>

    @Query("SELECT * from unlock_event_table WHERE event_time_milli > :event ORDER BY eventId")
    fun getUnlocksAfterTime(event : Long) : LiveData<List<UnlockEvent>>

    @Query("SELECT COUNT(*) from  unlock_event_table WHERE event_time_milli > :event ")
    fun getTodayUnlocksCountAfterTime(event: Long) : LiveData<Int>

    @Query("SELECT COUNT(*) from  unlock_event_table WHERE event_time_milli BETWEEN :start AND :end ")
    fun getUnlocksCountBetweenTimes(start : Long, end : Long) : LiveData<Int>

    @Query("SELECT COUNT(*) from  unlock_event_table WHERE event_time_milli > :event ")
    suspend fun getTodayUnlocksCountAfterTimeNoLiveData(event: Long) : Int

    @Query("SELECT * FROM unlock_event_table WHERE event_time_milli > :time ORDER BY eventId DESC")
    fun getAllUnlcoksFromTime(time : Long): LiveData<List<UnlockEvent>>

    @Query("SELECT * FROM unlock_event_table WHERE event_time_milli > :time ORDER BY eventId DESC")
    suspend fun getAllUnlcoksFromTimeNoLiveData(time : Long): List<UnlockEvent>

    @Query("SELECT COUNT(*) from  unlock_event_table ")
    suspend fun getTableCount():Int

    @Query("SELECT * FROM unlock_event_table WHERE event_time_milli BETWEEN :start AND :end ORDER BY eventId DESC ")
     fun getAllUnlocksBetweenTwoTimes(start : Long, end : Long) : LiveData<List<UnlockEvent>>
}