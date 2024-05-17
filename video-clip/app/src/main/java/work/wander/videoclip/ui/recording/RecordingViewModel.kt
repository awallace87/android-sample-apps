package work.wander.videoclip.ui.recording

import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import work.wander.videoclip.domain.video.RecorderState
import work.wander.videoclip.domain.video.VideoRecorder
import work.wander.videoclip.framework.camerax.CameraXManager
import work.wander.videoclip.framework.camerax.ForCameraX
import work.wander.videoclip.framework.logging.AppLogger
import javax.inject.Inject

data class CameraSelectionInfo(
    val displayText: String,
    val cameraInfo: CameraInfo?,
    val cameraSelector: CameraSelector?,
) {
    companion object {
        val Unspecified: CameraSelectionInfo = CameraSelectionInfo(
            displayText = "Default Camera",
            cameraInfo = null,
            cameraSelector = null,
        )
    }
}

sealed interface VideoRecordingState {
    object Initial : VideoRecordingState
    object Ready : VideoRecordingState
    object Starting : VideoRecordingState
    object Recording : VideoRecordingState
    object Stopping : VideoRecordingState
    object Stopped : VideoRecordingState
}

@HiltViewModel
class RecordingViewModel @Inject constructor(
    val lifecycleCameraController: LifecycleCameraController,
    private val cameraXManager: CameraXManager,
    @ForCameraX private val cameraXCoroutineScope: CoroutineScope,
    private val videoRecorder: VideoRecorder,
    private val appLogger: AppLogger,
) : ViewModel() {

    private val selectedCameraDevice: MutableStateFlow<CameraSelectionInfo> =
        MutableStateFlow(CameraSelectionInfo.Unspecified)

    val selectedCamera: StateFlow<CameraSelectionInfo> = selectedCameraDevice

    val availableCameras: StateFlow<List<CameraSelectionInfo>> = flow {
        var camNum = 0
        emit(cameraXManager.getAvailableCameraDevices().await().map {
            CameraSelectionInfo(
                displayText = "Cam ${camNum++} - Facing: ${it.lensFacing}",
                cameraInfo = it,
                cameraSelector = it.cameraSelector,
            )
        })
    }.stateIn(
        cameraXCoroutineScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val videoRecordingState = videoRecorder.getRecorderState()
        .map { recorderState ->
            when (recorderState) {
                is RecorderState.Idle -> VideoRecordingState.Ready
                is RecorderState.Initializing -> VideoRecordingState.Starting
                is RecorderState.RecordingStart -> VideoRecordingState.Starting
                is RecorderState.RecordingActive -> VideoRecordingState.Recording
                is RecorderState.RecordingPaused -> VideoRecordingState.Recording
                is RecorderState.RecordingResumed -> VideoRecordingState.Recording
                is RecorderState.RecordingStopping -> VideoRecordingState.Stopping
                is RecorderState.RecordingFinished -> VideoRecordingState.Stopped
                is RecorderState.Error -> VideoRecordingState.Stopped
            }
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = VideoRecordingState.Initial
        )

    fun getVideoRecordingState(): StateFlow<VideoRecordingState> = videoRecordingState

    fun startRecording() {
        appLogger.info("Recording started")
        cameraXCoroutineScope.launch {
            val didStartRecording = videoRecorder.startRecording().await()

            if (didStartRecording) {
                appLogger.info("Recording started successfully")
            } else {
                appLogger.error("Failed to start recording")
                // TODO send Toast
            }
        }
    }

    fun stopRecording() {
        appLogger.info("Recording stopped")
        cameraXCoroutineScope.launch {
            val didStopRecording = videoRecorder.stopRecording().await()
            appLogger.info("Recording stopped successfully: $didStopRecording")
        }
    }

    fun cameraDeviceSelected(cameraInfo: CameraSelectionInfo) {
        appLogger.info("Camera selected: $cameraInfo")
        cameraInfo.cameraSelector?.let {
            selectedCameraDevice.update { cameraInfo }
            lifecycleCameraController.cameraSelector = it
        }
            ?: appLogger.error("Attempting switch cameras with no valid Camera Selector ($cameraInfo)")
    }

}
