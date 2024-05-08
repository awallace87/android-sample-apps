package work.wander.directory.data.roomdemo

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {

    @TypeConverter
    fun fromTimestamp(epochMillis: Long): Instant {
        return Instant.ofEpochMilli(epochMillis)
    }

    @TypeConverter
    fun toTimestamp(instant: Instant): Long {
        return instant.toEpochMilli()
    }
}