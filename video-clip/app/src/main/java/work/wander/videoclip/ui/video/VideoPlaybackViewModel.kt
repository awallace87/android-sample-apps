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
import work.wander.videoclip.framework.annotation.BackgroundThread
import work.wander.videoclip.framework.logging.AppLogger
import javax.inject.Inject

sealed interface VideoPlaybackUiState {
    object Initial : VideoPlaybackUiState
    data class LoadingMedia(val loadingMessage: String) : VideoPlaybackUiState
    data class Error(val errorMessage: String) : VideoPlaybackUiState
    data class PlayerReady(
        val exoPlayer: ExoPlayer,
    ) : VideoPlaybackUiState
}

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer,
    private val videoPlaybackRepository: VideoRecordingsRepository,
    @BackgroundThread private val coroutineDispatcher: CoroutineDispatcher,
    @MainThread private val coroutineDispatcherMain: CoroutineDispatcher,
    private val appLogger: AppLogger,
) : ViewModel() {

    private val videoPlaybackUiState =
        MutableStateFlow<VideoPlaybackUiState>(VideoPlaybackUiState.Initial)

    fun setVideoId(videoRecordingId: Long) {
        if (videoRecordingId == NO_VIDEO_SPECIFIED) {
            videoPlaybackUiState.update { VideoPlaybackUiState.Error("No video specified for playback.") }
            return
        } else {
            appLogger.info("Setting video repository ID for playback: $videoRecordingId")
            videoPlaybackUiState.update {
                VideoPlaybackUiState.LoadingMedia("Loading Data for Recording (${videoRecordingId})")
            }
            viewModelScope.launch(coroutineDispatcher) {
                val videoRecording = videoPlaybackRepository.getRecordingById(videoRecordingId)
                if (videoRecording != null) {
                    appLogger.info("Video found for playback: ${videoRecording.videoFilePath}")
                    setVideoFileForPlayback(videoRecording.videoFilePath)
                } else {
                    appLogger.error("No data found for recording ID: $videoRecordingId")
                    videoPlaybackUiState.update { VideoPlaybackUiState.Error("Video not found") }
                }
            }
        }
    }

    private fun setVideoFileForPlayback(videoFilePath: String) {
        videoPlaybackUiState.update { VideoPlaybackUiState.LoadingMedia("Loading ($videoFilePath) for playback") }
        // ExoPlayer requires the media item to be set on the main thread
        // See: (https://developer.android.com/guide/topics/media/issues/player-accessed-on-wrong-thread)
        viewModelScope.launch(coroutineDispatcherMain) {
            val videoFile = MediaItem.fromUri(videoFilePath)
            exoPlayer.playWhenReady = false
            exoPlayer.stop()
            exoPlayer.setMediaItem(videoFile)
            exoPlayer.prepare()
            videoPlaybackUiState.update {
                VideoPlaybackUiState.PlayerReady(
                    exoPlayer = exoPlayer
                )
            }
        }
    }


    fun getVideoPlaybackUiState(): StateFlow<VideoPlaybackUiState> {
        return videoPlaybackUiState
    }

    companion object {
        const val NO_VIDEO_SPECIFIED = Long.MIN_VALUE
    }
}