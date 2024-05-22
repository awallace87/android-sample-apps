package work.wander.pomodogetter.framework.time

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun LocalDate.toFormattedString(): String {
    val dayOfMonth = this.dayOfMonth
    val dayOfMonthSuffix = when (dayOfMonth) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }

    val month = this.month.getDisplayName(TextStyle.FULL, Locale.US)
    val year = this.year

    return "$month ${dayOfMonth}${dayOfMonthSuffix} $year"
}