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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import work.wander.videoclip.data.recordings.VideoRecordingsRepository
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity
import work.wander.videoclip.framework.annotation.BackgroundThread
import work.wander.videoclip.framework.camerax.ForCameraX
import work.wander.videoclip.framework.clock.AppClock
import work.wander.videoclip.framework.logging.AppLogger
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * CameraX focused/based implementation of [VideoRecorder].
 */
class CameraXVideoRecorder @Inject constructor(
    @ForVideoRecording private val videoRecordingDirectory: File,
    private val lifecycleCameraController: LifecycleCameraController,
    private val videoRecordingRepository: VideoRecordingsRepository,
    private val previewImageUpdater: PreviewImageUpdater,
    @ForCameraX private val cameraXCoroutineScope: CoroutineScope,
    @MainThread private val mainDispatcher: CoroutineDispatcher,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher,
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
                val epochMillis = appClock.currentEpochTimeMillis()
                val outputFile = createNewOutputFile(epochMillis)

                val recordingEntity = withContext(backgroundDispatcher) {
                    videoRecordingRepository.saveNewRecording(
                        outputFile.absolutePath,
                        epochMillis
                    )
                }
                if (recordingEntity == null) {
                    appLogger.error("Error saving initial recording entity")
                    currentRecorderState.update {
                        RecorderState.Error("Error saving recording entity")
                    }
                    return@async false
                }

                appLogger.info("Starting Video Recording to File: $outputFile")
                val recording = lifecycleCameraController.startRecording(
                    FileOutputOptions.Builder(outputFile)
                        .build(),
                    AudioConfig.create(false),
                    executor
                ) { value -> onVideoRecordEvent(value, recordingEntity) }
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

    // TODO: This should be separated out into a separate class/handler.
    private fun onVideoRecordEvent(
        event: VideoRecordEvent,
        videoRecordingEntity: VideoRecordingEntity
    ) {
        // Update Internal State
        when (event) {
            is VideoRecordEvent.Status -> {
                appLogger.info("Recording continues: ${event.recordingStats}")
                currentRecorderState.update {
                    when (val currentState = it) {
                        // Expected states
                        is RecorderState.RecordingStart -> {
                            appLogger.info("Recording active from start")
                            RecorderState.RecordingActive(
                                recording = currentState.recording,
                                recordingDurationMillis = TimeUnit.NANOSECONDS.toMillis(event.recordingStats.recordedDurationNanos)
                            )
                        }

                        is RecorderState.RecordingActive -> {
                            appLogger.info("Recording active from active, most common")

                            RecorderState.RecordingActive(
                                recording = currentState.recording,
                                recordingDurationMillis = TimeUnit.NANOSECONDS.toMillis(event.recordingStats.recordedDurationNanos)
                            )
                        }

                        is RecorderState.RecordingResumed -> {
                            appLogger.info("Recording active from resumed")
                            RecorderState.RecordingActive(
                                recording = currentState.recording,
                                recordingDurationMillis = TimeUnit.NANOSECONDS.toMillis(event.recordingStats.recordedDurationNanos)
                            )
                        }
                        is RecorderState.RecordingStopping -> {
                            appLogger.warn("Recording active from stopping, ignoring")
                            currentState
                        }
                        // Unexpected states
                        is RecorderState.RecordingPaused,
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
                    when (val currentState = it) {
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
                // Update Preview Image (only if recording was successful)
                cameraXCoroutineScope.launch(backgroundDispatcher) {

                    val didCreateThumbnail =
                        previewImageUpdater.createAndUpdatePreviewImageFor(videoRecordingEntity.videoFilePath, videoRecordingEntity.id).await()
                    if (!didCreateThumbnail) {
                        appLogger.error("Failed to create thumbnail for video: ${videoRecordingEntity.videoFilePath}")
                    } else {
                        appLogger.debug("Thumbnail created for video: ${videoRecordingEntity.videoFilePath}")
                    }
                }
            }

            is VideoRecordEvent.Pause -> {
                appLogger.info("Recording pause event received")
                currentRecorderState.update {
                    when (val currentState = it) {
                        // Expected states
                        is RecorderState.RecordingActive -> {
                            appLogger.info("Recording paused from active")
                            RecorderState.RecordingPaused(
                                currentState.recordingDurationMillis,
                                currentState.recording
                            )
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
                    when (val currentState = it) {
                        // Expected states
                        is RecorderState.RecordingPaused -> {
                            appLogger.info("Recording resumed from paused")
                            RecorderState.RecordingResumed(
                                currentState.recordingDurationMillis,
                                currentState.recording
                            )
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
                    when (val currentState = it) {
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
        // Update Database
        cameraXCoroutineScope.launch(backgroundDispatcher) {
            val recordingStatus = when (event) {
                is VideoRecordEvent.Start -> VideoRecordingEntity.RecordingStatus.STARTED
                is VideoRecordEvent.Finalize -> VideoRecordingEntity.RecordingStatus.SAVED
                is VideoRecordEvent.Pause -> VideoRecordingEntity.RecordingStatus.PAUSED
                is VideoRecordEvent.Resume -> VideoRecordingEntity.RecordingStatus.RESUMED
                is VideoRecordEvent.Status -> VideoRecordingEntity.RecordingStatus.RECORDING
                else -> VideoRecordingEntity.RecordingStatus.ERROR
            }
            videoRecordingRepository.updateRecording(
                videoRecordingEntity.copy(
                    recordingStatus = recordingStatus,
                    sizeInBytes = event.recordingStats.numBytesRecorded,
                    recordedDurationMillis = TimeUnit.NANOSECONDS.toMillis(event.recordingStats.recordedDurationNanos)
                )
            )
        }


    }

    private fun createNewOutputFile(timestamp: Long): File {
        return videoRecordingDirectory.resolve("video-$timestamp.mp4").also {
            it.createNewFile()
        }
    }
}