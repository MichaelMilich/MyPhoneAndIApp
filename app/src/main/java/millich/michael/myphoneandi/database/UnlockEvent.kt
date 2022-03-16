package millich.michael.myphoneandi.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unlock_event_table")
data class UnlockEvent(
    @PrimaryKey(autoGenerate = true)
    var eventId: Long = 0L,
    @ColumnInfo(name ="event_time_milli")
    var eventTime: Long =System.currentTimeMillis()
)