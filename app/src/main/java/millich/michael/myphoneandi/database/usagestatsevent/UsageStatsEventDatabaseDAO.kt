package millich.michael.myphoneandi.database.usagestatsevent

import android.app.usage.UsageEvents
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsageStatsEventDatabaseDAO {
    @Insert
    suspend fun Insert(event: UsageStatsEvent)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param event new value to write
     */
    @Update
    suspend fun update(event: UsageStatsEvent)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from UsageStatsEvent WHERE eventId = :key")
    suspend fun get(key: Long): UsageStatsEvent

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM UsageStatsEvent")
    suspend fun clear()


    /**
     * Selects and returns the latest unlock event.
     */
    @Query("SELECT * FROM UsageStatsEvent ORDER BY eventId DESC LIMIT 1")
    suspend fun getLastEvent(): UsageStatsEvent?
}