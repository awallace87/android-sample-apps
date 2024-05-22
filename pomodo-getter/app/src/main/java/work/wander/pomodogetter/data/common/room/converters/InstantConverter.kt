package work.wander.pomodogetter.data.common.room.converters

import androidx.room.TypeConverter
import java.time.Instant

object InstantConverter {

    @TypeConverter
    fun fromInstant(value: Instant): Long {
        return value.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

}