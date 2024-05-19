package work.wander.videoclip.data.recordings

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity

@Dao
interface VideoRecordingDao {

    @Query("SELECT * FROM video_recordings")
    fun getAll(): Flow<List<VideoRecordingEntity>>

    @Query("SELECT * FROM video_recordings WHERE id = :id")
    suspend fun getRecordingById(id: Long) : VideoRecordingEntity?

    @Update
    suspend fun updateEntity(videoRecordingEntity: VideoRecordingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(videoRecordingEntity: VideoRecordingEntity) : Long

    @Delete
    suspend fun deleteEntity(videoRecordingEntity: VideoRecordingEntity)

}