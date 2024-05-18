package work.wander.videoclip.domain.video

import androidx.camera.video.Recording
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.StateFlow

interface VideoRecorder {

    fun startRecording() : Deferred<Boolean>

    fun stopRecording() :  Deferred<Boolean>

    fun getRecorderState() : StateFlow<RecorderState>

}

sealed interface RecorderState {
    object Idle : RecorderState
    data class Initializing(
        val recording: Recording
    ) : RecorderState
    data class RecordingStart(
        val recording: Recording
    ): RecorderState
    data class RecordingActive(
        val recordingDurationMillis: Long,
        val recording: Recording
    ) : RecorderState
    data class RecordingPaused(
        val recordingDurationMillis: Long,
        val recording: Recording
    ) : RecorderState
    data class RecordingResumed(
        val recordingDurationMillis: Long,
        val recording: Recording
    ) : RecorderState
    object RecordingStopping : RecorderState
    object RecordingFinished : RecorderState
    data class Error(val errorMessage: String) : RecorderState
}


