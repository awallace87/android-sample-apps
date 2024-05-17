package work.wander.videoclip.domain.video

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import work.wander.videoclip.framework.annotation.BackgroundThread
import work.wander.videoclip.framework.camerax.ForCameraX
import work.wander.videoclip.framework.clock.AppClock
import work.wander.videoclip.framework.logging.AppLogger
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

class CameraXVideoRecorder @Inject constructor(
    @ForVideoRecording private val videoRecordingDirectory: File,
    private val lifecycleCameraController: LifecycleCameraController,
    @ForCameraX private val cameraXCoroutineScope: CoroutineScope,
    @ForCameraX private val cameraXDispatcher: CoroutineDispatcher,
    @MainThread private val mainDispatcher: CoroutineDispatcher,
    @BackgroundThread private val executor: Executor,
    private val appLogger: AppLogger,
    private val appClock: AppClock
) : VideoRecorder {

    private val currentRecorderState =
        MutableStateFlow<RecorderState>(RecorderState.Idle)

    override fun getRecorderState(): StateFlow<RecorderState> = currentRecorderState

    @SuppressLint("MissingPermission")
    override fun startRecording(): Deferred<Boolean> {
        return cameraXCoroutineScope.async(mainDispatcher) {
            when (val currentState = currentRecorderState.value) {
                is RecorderState.Initializing,
                is RecorderState.RecordingStart,
                is RecorderState.RecordingActive,
                is RecorderState.RecordingPaused,
                is RecorderState.RecordingResumed,
                is RecorderState.RecordingStopping -> {
                    appLogger.error("Recording start requested while in invalid state: $currentState")
                    return@async false
                }
                is RecorderState.Error -> {
                    appLogger.warn("Recording start requested while in error state: (${currentState.errorMessage})")
                }
                is RecorderState.RecordingFinished,
                is RecorderState.Idle -> {
                    appLogger.info("Starting Video Recording")
                }

            }
            try {
                val outputFile = createNewOutputFile()
                appLogger.info("Starting Video Recording to File: $outputFile")
                val recording = lifecycleCameraController.startRecording(
                    FileOutputOptions.Builder(outputFile)
                        .build(),
                    AudioConfig.create(false),
                    executor
                ) { value -> onVideoRecordEvent(value) }
                currentRecorderState.update {
                    RecorderState.Initializing(recording)
                }
                return@async true
            } catch (e: Exception) {
                appLogger.error(e, "Error starting video recording")
                currentRecorderState.update {
                    RecorderState.Error("Error starting video recording")
                }
                return@async false
            }
        }
    }

    override fun stopRecording(): Deferred<Boolean> {
        return cameraXCoroutineScope.async(mainDispatcher) {
            when (val currentState = currentRecorderState.value) {
                is RecorderState.Initializing,
                is RecorderState.RecordingStart,
                is RecorderState.RecordingPaused,
                is RecorderState.RecordingResumed,
                is RecorderState.RecordingStopping -> {
                    appLogger.error("Recording stop requested while in invalid state: $currentState")
                    return@async false
                }
                is RecorderState.Error -> {
                    appLogger.warn("Recording stop requested while in error state: (${currentState.errorMessage})")
                }
                is RecorderState.RecordingFinished,
                is RecorderState.Idle -> {
                    appLogger.warn("Recording stop requested while in invalid state: $currentState")
                    return@async false
                }
                is RecorderState.RecordingActive -> {
                    appLogger.info("Stopping Video Recording")
                }
            }
            try {
                val recording = when (val currentState = currentRecorderState.value) {
                    is RecorderState.RecordingActive -> currentState.recording
                    else -> {
                        appLogger.error("Invalid state to stop recording: $currentState")
                        return@async false
                    }
                }
                recording.stop()
                currentRecorderState.update {
                    RecorderState.RecordingStopping
                }
                return@async true
            } catch (e: Exception) {
                appLogger.error(e, "Error stopping video recording")
                currentRecorderState.update {
                    RecorderState.Error("Error stopping video recording")
                }
                return@async false
            }
        }
    }

    private fun onVideoRecordEvent(event: VideoRecordEvent?) {
        when (event) {
            is VideoRecordEvent.Status -> {
                appLogger.info("Recording continues: ${event.recordingStats}")
                currentRecorderState.update {
                    when (val currentState = it) {
                        // Expected states
                        is RecorderState.RecordingStart -> {
                            appLogger.info("Recording active from start")
                            RecorderState.RecordingActive(currentState.recording)
                        }
                        is RecorderState.RecordingActive -> {
                            appLogger.info("Recording active from active, most common")

                            RecorderState.RecordingActive(currentState.recording)
                        }
                        is RecorderState.RecordingResumed -> {
                            appLogger.info("Recording active from resumed")
                            RecorderState.RecordingActive(currentState.recording)
                        }
                        // Unexpected states
                        is RecorderState.RecordingPaused,
                        is RecorderState.RecordingStopping,
                        is RecorderState.RecordingFinished,
                        is RecorderState.Error,
                        is RecorderState.Initializing,
                        is RecorderState.Idle -> {
                            appLogger.warn("Invalid state to receive recording status: $currentState")
                            currentState
                        }

                    }

                }
            }

            is VideoRecordEvent.Finalize -> {
                appLogger.info("Recording finalize event received")
                currentRecorderState.update {
                    when(val currentState = it) {
                        // Expected states
                        is RecorderState.RecordingStopping -> {
                            appLogger.info("Recording finalized from stopping")
                            RecorderState.RecordingFinished
                        }
                        is RecorderState.RecordingActive -> {
                            appLogger.warn("Recording finalized from active state, directly")
                            RecorderState.RecordingFinished
                        }
                        is RecorderState.RecordingPaused,
                        is RecorderState.RecordingResumed,
                        is RecorderState.RecordingStart,
                        is RecorderState.RecordingFinished,
                        is RecorderState.Error,
                        is RecorderState.Initializing,
                        is RecorderState.Idle -> {
                            appLogger.warn("Invalid state to receive recording finalize event: $currentState. Ignoring.")
                            currentState
                        }
                    }
                }
            }

            is VideoRecordEvent.Pause -> {
                appLogger.info("Recording pause event received")
                currentRecorderState.update {
                    when(val currentState = it) {
                        // Expected states
                        is RecorderState.RecordingActive -> {
                            appLogger.info("Recording paused from active")
                            RecorderState.RecordingPaused(currentState.recording)
                        }
                        // Unexpected states
                        is RecorderState.RecordingPaused,
                        is RecorderState.RecordingStopping,
                        is RecorderState.RecordingFinished,
                        is RecorderState.Error,
                        is RecorderState.Initializing,
                        is RecorderState.Idle,
                        is RecorderState.RecordingStart,
                        is RecorderState.RecordingResumed -> {
                            appLogger.warn("Invalid state to receive recording pause event: $currentState. Ignoring.")
                            currentState
                        }
                    }
                }
            }

            is VideoRecordEvent.Resume -> {
                appLogger.info("Recording resumed")
                currentRecorderState.update {
                    when(val currentState = it) {
                        // Expected states
                        is RecorderState.RecordingPaused -> {
                            appLogger.info("Recording resumed from paused")
                            RecorderState.RecordingResumed(currentState.recording)
                        }
                        // Unexpected states
                        is RecorderState.RecordingResumed,
                        is RecorderState.RecordingStopping,
                        is RecorderState.RecordingFinished,
                        is RecorderState.Error,
                        is RecorderState.Initializing,
                        is RecorderState.Idle,
                        is RecorderState.RecordingStart,
                        is RecorderState.RecordingActive -> {
                            appLogger.warn("Invalid state to receive recording resume event: $currentState. Ignoring.")
                            currentState
                        }
                    }
                }
            }

            is VideoRecordEvent.Start -> {
                appLogger.info("Recording started")
                currentRecorderState.update {
                    when(val currentState = it) {
                        // Expected states
                        is RecorderState.Initializing -> {
                            appLogger.info("Recording started from initializing")
                            RecorderState.RecordingStart(currentState.recording)
                        }
                        // Unexpected states
                        is RecorderState.RecordingStart,
                        is RecorderState.RecordingActive,
                        is RecorderState.RecordingPaused,
                        is RecorderState.RecordingResumed,
                        is RecorderState.RecordingStopping,
                        is RecorderState.RecordingFinished,
                        is RecorderState.Error,
                        is RecorderState.Idle -> {
                            appLogger.warn("Invalid state to receive recording start event: $currentState. Ignoring.")
                            currentState
                        }
                    }
                }
            }
        }
    }

    private fun createNewOutputFile(): File {
        val epochMillis = appClock.currentEpochTimeMillis()
        return videoRecordingDirectory.resolve("video-$epochMillis.mp4").also {
            it.createNewFile()
        }
    }
}