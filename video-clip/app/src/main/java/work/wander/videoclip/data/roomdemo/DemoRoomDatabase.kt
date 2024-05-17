package work.wander.videoclip.data.roomdemo

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import work.wander.videoclip.data.roomdemo.entity.DemoEntity

@Database(entities = [DemoEntity::class], version = 1, exportSchema = false)
@TypeConverters(InstantConverter::class)
abstract class DemoRoomDatabase : RoomDatabase() {
    abstract fun demoEntityDao(): DemoEntityDao
}