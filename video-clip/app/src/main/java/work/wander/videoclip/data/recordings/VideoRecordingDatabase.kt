package work.wander.videoclip.data.recordings

import androidx.room.Database
import androidx.room.RoomDatabase
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity

@Database(entities = [VideoRecordingEntity::class], version = 1, exportSchema = false)
abstract class VideoRecordingDatabase : RoomDatabase() {

    abstract fun videoRecordingDao(): VideoRecordingDao
}