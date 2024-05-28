package work.wander.wikiview.data.common.room.converters

import androidx.room.TypeConverter
import kotlin.time.Duration

/**
 * Converts a [Duration] to a [String] and vice versa.
 */
object DurationConverter {

    @TypeConverter
    fun fromDuration(value: String): Duration {
        return Duration.parse(value)
    }

    @TypeConverter
    fun durationToTimestamp(duration: Duration): String {
        return duration.toString()
    }
}