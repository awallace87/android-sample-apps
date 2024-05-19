package work.wander.videoclip.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import work.wander.videoclip.data.recordings.VideoRecordingsRepository
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class PreviousRecordingItem(
    val videoRepositoryId: Long,
    val durationInMillis: Long,
    val sizeInBytes: Long,
    val captureStartedAtEpochMillis: Long,
    val thumbnailPath: String,
    val videoFilePath: String,
    val recordingStatus: VideoRecordingEntity.RecordingStatus = VideoRecordingEntity.RecordingStatus.INITIAL,
) {
    fun captureStartedFormatted(): String =
        CAPTURE_STARTED_FORMATTER.format(Date(captureStartedAtEpochMillis))

    fun durationFormatted(): String {
        val duration = durationInMillis.toDuration(DurationUnit.MILLISECONDS)
        return String.format(
            "%02d:%02d:%02d:%03d",
            duration.inWholeHours,
            duration.inWholeMinutes % 60,
            duration.inWholeSeconds % 60,
            duration.inWholeMilliseconds % 1000
        )
    }

    fun formatSizeInMb(): String {
        val sizeInMb = sizeInBytes / (1024.0 * 1024.0)
        return String.format("%.2f MB", sizeInMb)
    }

    companion object {
        private const val CAPTURE_STARTED_FORMAT = "MMMM d, yyyy - HH:mm"
        private const val DURATION_TIME_FORMAT = "%02d:%02d:%02d:%03d"
        private val CAPTURE_STARTED_FORMATTER =
            SimpleDateFormat(CAPTURE_STARTED_FORMAT, Locale.getDefault())
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    videoRecordingsRepository: VideoRecordingsRepository,
) : ViewModel() {

    private val previousRecordings = videoRecordingsRepository.getPreviousRecordings()
        .map { recordings ->
            recordings.map { recording ->
                PreviousRecordingItem(
                    videoRepositoryId = recording.id,
                    durationInMillis = recording.recordedDurationMillis,
                    sizeInBytes = recording.sizeInBytes,
                    captureStartedAtEpochMillis = recording.timeCapturedEpochMillis,
                    thumbnailPath = recording.thumbnailFilePath,
                    videoFilePath = recording.videoFilePath,
                    recordingStatus = recording.recordingStatus
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun getPreviousRecordings() = previousRecordings


}