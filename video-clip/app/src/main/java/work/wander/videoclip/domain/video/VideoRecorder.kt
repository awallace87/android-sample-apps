package work.wander.videoclip.domain.video

import androidx.camera.video.Recording
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for recording video files.
 */
interface VideoRecorder {

    /**
     * Starts recording a video file.
     *
     * @return A [Deferred] that will complete with `true` if the recording started successfully, or `false` if there was an error.
     */
    fun startRecording() : Deferred<Boolean>

    /**
     * Pauses the current recording.
     *
     * @return A [Deferred] that will complete with `true` if the recording was paused successfully, or `false` if there was an error.
     */
    fun stopRecording() :  Deferred<Boolean>

    /**
     * Pauses the current recording.
     *
     * @return A [Deferred] that will complete with `true` if the recording was paused successfully, or `false` if there was an error.
     */
    fun getRecorderState() : StateFlow<RecorderState>

}

/**
* `RecorderState` is a sealed interface that represents the different states of a video recorder.
*
* The different states are:
* - `Idle`: The recorder is not currently recording.
* - `Initializing`: The recorder is preparing to start recording. Contains a `Recording` object.
* - `RecordingStart`: The recorder has started recording. Contains a `Recording` object.
* - `RecordingActive`: The recorder is currently recording. Contains the recording duration in milliseconds and a `Recording` object.
* - `RecordingPaused`: The recorder has paused recording. Contains the recording duration in milliseconds and a `Recording` object.
* - `RecordingResumed`: The recorder has resumed recording after being paused. Contains the recording duration in milliseconds and a `Recording` object.
* - `RecordingStopping`: The recorder is stopping the recording.
* - `RecordingFinished`: The recorder has finished recording.
* - `Error`: An error occurred. Contains an error message.
*/
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


