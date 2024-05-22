package work.wander.wikiview.data.tasks

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import work.wander.wikiview.data.common.room.converters.InstantConverter
import work.wander.wikiview.data.common.room.converters.LocalDateConverter
import work.wander.wikiview.data.tasks.entity.TaskDataEntity

/**
 * The Room database for Task data.
 */
@Database(entities = [TaskDataEntity::class], version = 1, exportSchema = false)
@TypeConverters(value = [InstantConverter::class, LocalDateConverter::class])
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}