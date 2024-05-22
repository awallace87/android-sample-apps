package work.wander.wikiview.data.common.room.converters

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Converts a [LocalDate] to a [String] and vice versa.
 */
object LocalDateConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }
}