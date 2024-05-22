package work.wander.pomodogetter.data.pomodoro

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import work.wander.pomodogetter.data.common.room.converters.DurationConverter
import work.wander.pomodogetter.data.common.room.converters.InstantConverter
import work.wander.pomodogetter.data.pomodoro.entity.CompletedPomodoro
import work.wander.pomodogetter.data.pomodoro.entity.InProgressPomodoro

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