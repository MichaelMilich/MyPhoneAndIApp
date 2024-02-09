package millich.michael.myphoneandi.database.usagestatsevent

import android.app.usage.UsageEvents
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UsageStatsEvent (
    @PrimaryKey(autoGenerate = true)
    var eventId : Int = 0,
    @ColumnInfo var timestamp : Long =0,
    @ColumnInfo var eventType : Int =0,
    @ColumnInfo var packageName : String =""
) {
    companion object {
        fun fromUsageEvent( event: UsageEvents.Event ) : UsageStatsEvent {
            return UsageStatsEvent(timestamp = event.timeStamp , eventType = event.eventType, packageName = event.packageName)
        }
    }
}