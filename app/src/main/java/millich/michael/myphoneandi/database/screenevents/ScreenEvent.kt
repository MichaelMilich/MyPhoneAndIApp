package millich.michael.myphoneandi.database.screenevents

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.IllegalArgumentException

@Entity(tableName = "screen_event_table")
data class ScreenEvent(
    @PrimaryKey(autoGenerate = true)
    var eventId: Long = 0L,
    @ColumnInfo(name ="event_time_milli")
    var eventTime: Long =System.currentTimeMillis(),
    @ColumnInfo(name = "event_type")
    var eventType: String, // "off" or "unlock"
)

enum class ScreenEventType(val value: String){
    ScreenOn("ScreenOn"),
    ScreenOff("ScreenOff");

    companion object{
        fun fromValue(value: String) : ScreenEventType {
            return when(value){
                "ScreenOn" -> ScreenOn
                "ScreenOff" -> ScreenOff
                else -> throw IllegalArgumentException()
            }
        }
    }
}