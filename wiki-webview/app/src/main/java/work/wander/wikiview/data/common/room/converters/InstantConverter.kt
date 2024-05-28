package work.wander.wikiview.data.common.room.converters

import androidx.room.TypeConverter
import java.time.Instant

/**
 * Converts an [Instant] to a [Long] and vice versa.
 */
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