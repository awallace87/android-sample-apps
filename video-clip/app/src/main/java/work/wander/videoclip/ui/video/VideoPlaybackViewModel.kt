package work.wander.videoclip.ui.video

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import work.wander.videoclip.data.recordings.VideoRecordingsRepository
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity
import work.wander.videoclip.framework.annotation.BackgroundThread
import work.wander.videoclip.framework.logging.AppLogger
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed interface VideoPlaybackUiState {
    object Initial : VideoPlaybackUiState
    data class LoadingMedia(val loadingMessage: String) : VideoPlaybackUiState
    data class Error(val errorMessage: String) : VideoPlaybackUiState
    data class PlayerReady(
        val exoPlayer: ExoPlayer,
        val videoRecordingEntity: VideoRecordingEntity
    ) : VideoPlaybackUiState {
        fun getTitleText(): String {
            return FORMATTER.format(
                LocalDateTime.ofEpochSecond(
                    TimeUnit.MILLISECONDS.toSeconds(
                        videoRecordingEntity.timeCapturedEpochMillis
                    ), 0, ZoneOffset.UTC
                )
            )
        }

        companion object {
            const val DATE_FORMAT = "yyyy-MM-dd HH:mm"
            val FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT)
        }
    }
}

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val videoPlaybackRepository: VideoRecordingsRepository,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher,
    @MainThread private val mainDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger,
) : ViewModel() {

    private val videoPlaybackUiState =
        MutableStateFlow<VideoPlaybackUiState>(VideoPlaybackUiState.Initial)

    fun getVideoPlaybackUiState(): StateFlow<VideoPlaybackUiState> {
        return videoPlaybackUiState
    }

    fun setVideoId(videoRecordingId: Long) {
        if (videoRecordingId == NO_VIDEO_SPECIFIED) {
            videoPlaybackUiState.update { VideoPlaybackUiState.Error("No video specified for playback.") }
            return
        } else {
            appLogger.info("Setting video repository ID for playback: $videoRecordingId")
            videoPlaybackUiState.update {
                VideoPlaybackUiState.LoadingMedia("Loading Data for Recording (${videoRecordingId})")
            }
            viewModelScope.launch(backgroundDispatcher) {
                val videoRecording = videoPlaybackRepository.getRecordingById(videoRecordingId)
                if (videoRecording != null) {
                    appLogger.info("Video found for playback: ${videoRecording.videoFilePath}")
                    loadRecordingIntoPlayer(videoRecording)
                } else {
                    appLogger.error("No data found for recording ID: $videoRecordingId")
                    videoPlaybackUiState.update { VideoPlaybackUiState.Error("Video not found") }
                }
            }
        }
    }

    fun deleteVideoRecording(videoRecording: VideoRecordingEntity) {
        viewModelScope.launch(backgroundDispatcher) {
            val didDeleteVideo = Files.deleteIfExists(Paths.get(videoRecording.videoFilePath))
            val didDeleteThumbnail =
                Files.deleteIfExists(Paths.get(videoRecording.thumbnailFilePath))
            videoPlaybackRepository.deleteRecording(videoRecording)
            appLogger.info("Deleted Entity (${videoRecording.id}) - deleted video: $didDeleteVideo, deleted thumbnail: $didDeleteThumbnail")
        }
    }

    private fun loadRecordingIntoPlayer(videoRecording: VideoRecordingEntity) {
        videoPlaybackUiState.update { VideoPlaybackUiState.LoadingMedia("Loading ($videoRecording) for playback") }
        // ExoPlayer requires the media item to be set on the main thread
        // See: (https://developer.android.com/guide/topics/media/issues/player-accessed-on-wrong-thread)
        viewModelScope.launch(mainDispatcher) {
            val videoFile = MediaItem.fromUri(videoRecording.videoFilePath)
            exoPlayer.playWhenReady = false
            exoPlayer.stop()
            exoPlayer.setMediaItem(videoFile)
            exoPlayer.prepare()
            videoPlaybackUiState.update {
                VideoPlaybackUiState.PlayerReady(
                    exoPlayer = exoPlayer,
                    videoRecordingEntity = videoRecording
                )
            }
        }
    }

    companion object {
        const val NO_VIDEO_SPECIFIED = Long.MIN_VALUE
    }
}