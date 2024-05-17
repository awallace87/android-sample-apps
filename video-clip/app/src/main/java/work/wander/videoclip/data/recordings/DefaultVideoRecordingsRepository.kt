package work.wander.videoclip.data.recordings

import kotlinx.coroutines.flow.Flow
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity
import javax.inject.Inject

class DefaultVideoRecordingsRepository @Inject constructor(
    private val videoRecordingDatabase: VideoRecordingDatabase
): VideoRecordingsRepository {
    override fun getPreviousRecordings(): Flow<List<VideoRecordingEntity>> {
        return videoRecordingDatabase.videoRecordingDao().getAll()
    }

    override suspend fun saveRecording(videoRecordingEntity: VideoRecordingEntity) : Long{
        return videoRecordingDatabase.videoRecordingDao().insertEntity(videoRecordingEntity)
    }

    override suspend fun updateRecording(videoRecordingEntity: VideoRecordingEntity) {
        videoRecordingDatabase.videoRecordingDao().updateEntity(videoRecordingEntity)
    }

    override suspend fun deleteRecording(videoRecordingEntity: VideoRecordingEntity) {
        videoRecordingDatabase.videoRecordingDao().deleteEntity(videoRecordingEntity)
    }

    override suspend fun getRecordingById(videoRepositoryId: Long) : VideoRecordingEntity? {
        return videoRecordingDatabase.videoRecordingDao().getRecordingById(videoRepositoryId)
    }

    override suspend fun saveNewRecording(
        videoFilePath: String,
        captureStartedEpochMillis: Long,
    ) : VideoRecordingEntity? {
        val newRecording = VideoRecordingEntity.createNewRecording(captureStartedEpochMillis, videoFilePath)
        return videoRecordingDatabase.videoRecordingDao().getRecordingById(saveRecording(newRecording))
    }

}