package work.wander.videoclip.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import work.wander.videoclip.data.recordings.VideoRecordingsRepository
import javax.inject.Inject

data class PreviousRecordingItem(
    val durationInMillis: Long,
    val sizeInBytes: Long,
    val thumbnailPath: String,
    val videoFilePath: String,
    val recordingStatus: String = "Unspecified",
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val videoRecordingsRepository: VideoRecordingsRepository,
): ViewModel() {

    private val previousRecordings = videoRecordingsRepository.getPreviousRecordings()
        .map { recordings ->
            recordings.map { recording ->
                PreviousRecordingItem(
                    durationInMillis = recording.recordedDurationMillis,
                    sizeInBytes = recording.sizeInBytes,
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