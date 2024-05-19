package work.wander.videoclip.data.recordings.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_recordings")
data class VideoRecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timeCapturedEpochMillis: Long,
    val recordedDurationMillis: Long = 0,
    val recordingStatus: RecordingStatus = RecordingStatus.INITIAL,
    val videoFilePath: String = INITIAL_FILE_PATH,
    val thumbnailFilePath: String = INITIAL_THUMBNAIL_PATH,
    val sizeInBytes: Long = 0,
) {
    enum class RecordingStatus(val status: String) {
        INITIAL("Initial"),
        STARTING("Starting"),
        STARTED("Started"),
        PAUSED("Paused"),
        RESUMED("Resumed"),
        RECORDING("Recording"),
        SAVED("Saved"),
        FAILED("Failed"),
        ERROR("Error"),
    }

    companion object {
        const val INITIAL_FILE_PATH = "initial_path"
        const val INITIAL_THUMBNAIL_PATH = "initial_thumbnail_path"

        fun createNewRecording(
            epochMillis: Long,
            outputFilePath: String = INITIAL_FILE_PATH
        ): VideoRecordingEntity {
            return VideoRecordingEntity(
                timeCapturedEpochMillis = epochMillis,
                videoFilePath = outputFilePath,
                recordingStatus = RecordingStatus.STARTING,
            )
        }
    }
}


