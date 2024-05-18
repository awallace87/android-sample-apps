package work.wander.videoclip.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import work.wander.videoclip.data.recordings.VideoRecordingsRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class PreviousRecordingItem(
    val videoRepositoryId: Long,
    val durationInMillis: Long,
    val sizeInBytes: Long,
    val captureStartedAtEpochMillis: Long,
    val thumbnailPath: String,
    val videoFilePath: String,
    val recordingStatus: String = "Unspecified",
) {
    fun captureStartedFormatted(): String =
        CAPTURE_STARTED_FORMATTER.format(Date(captureStartedAtEpochMillis))

    fun durationFormatted(): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % TimeUnit.HOURS.toMinutes(1)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % TimeUnit.MINUTES.toSeconds(1)
        val millis = durationInMillis % TimeUnit.SECONDS.toMillis(1)
        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis)
    }

    fun formatSizeInMb(): String {
        val sizeInMb = sizeInBytes / (1024.0 * 1024.0)
        return String.format("%.2f MB", sizeInMb)
    }

    companion object {
        private const val CAPTURE_STARTED_FORMAT = "MMMM d, yyyy - HH:mm"
        private val CAPTURE_STARTED_FORMATTER =
            SimpleDateFormat(CAPTURE_STARTED_FORMAT, Locale.getDefault())
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRecordingsRepository: VideoRecordingsRepository,
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
                    recordingStatus = recording.recordingStatus.status
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