package work.wander.pomodogetter.data.common.room.converters

import androidx.room.TypeConverter
import kotlin.time.Duration

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