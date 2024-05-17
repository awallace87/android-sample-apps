package work.wander.videoclip.data.recordings

import kotlinx.coroutines.flow.Flow
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity

interface VideoRecordingsRepository {
    fun getPreviousRecordings(): Flow<List<VideoRecordingEntity>>

    suspend fun saveNewRecording(
        videoFilePath: String,
        captureStartedEpochMillis: Long,
    ) : VideoRecordingEntity?

    suspend fun saveRecording(videoRecordingEntity: VideoRecordingEntity) : Long

    suspend fun updateRecording(videoRecordingEntity: VideoRecordingEntity)

    suspend fun deleteRecording(videoRecordingEntity: VideoRecordingEntity)

    suspend fun getRecordingById(videoRepositoryId: Long) : VideoRecordingEntity?
}