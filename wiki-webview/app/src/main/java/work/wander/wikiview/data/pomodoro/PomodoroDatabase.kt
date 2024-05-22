package work.wander.wikiview.data.pomodoro

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import work.wander.wikiview.data.common.room.converters.DurationConverter
import work.wander.wikiview.data.common.room.converters.InstantConverter
import work.wander.wikiview.data.pomodoro.entity.CompletedPomodoro
import work.wander.wikiview.data.pomodoro.entity.InProgressPomodoro

/**
 * The Room database for Pomodoro data.
 */
@Database(
    entities = [CompletedPomodoro::class, InProgressPomodoro::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [InstantConverter::class, DurationConverter::class])
abstract class PomodoroDatabase : RoomDatabase() {

    abstract fun completedPomodoroDao(): CompletedPomodoroDao

    abstract fun inProgressPomodoroDao(): InProgressPomodoroDao
}