package work.wander.pomodogetter.data.tasks

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity

@Database(entities = [TaskDataEntity::class], version = 1, exportSchema = false)
@TypeConverters(value = [InstantConverter::class, LocalDateConverter::class])
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}