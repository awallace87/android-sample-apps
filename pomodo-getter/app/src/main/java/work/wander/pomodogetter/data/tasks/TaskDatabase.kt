package work.wander.pomodogetter.data.tasks

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import work.wander.pomodogetter.data.common.room.converters.DurationConverter
import work.wander.pomodogetter.data.common.room.converters.InstantConverter
import work.wander.pomodogetter.data.common.room.converters.LocalDateConverter
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity
import work.wander.pomodogetter.data.tasks.entity.TimedTaskDataEntity

/**
 * The Room database for Task data.
 */
@Database(entities = [TaskDataEntity::class, TimedTaskDataEntity::class], version = 2, exportSchema = false)
@TypeConverters(value = [InstantConverter::class, LocalDateConverter::class, DurationConverter::class])
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    abstract fun timedTaskDao(): TimedTaskDao
}