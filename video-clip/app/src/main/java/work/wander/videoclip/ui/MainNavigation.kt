package work.wander.videoclip.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import work.wander.videoclip.ui.home.HomeView
import work.wander.videoclip.ui.home.HomeViewModel
import work.wander.videoclip.ui.recording.RecordingScreenView
import work.wander.videoclip.ui.recording.RecordingViewModel
import work.wander.videoclip.ui.settings.ApplicationSettingsView
import work.wander.videoclip.ui.settings.ApplicationSettingsViewModel
import work.wander.videoclip.ui.video.VideoPlaybackView
import work.wander.videoclip.ui.video.VideoPlaybackViewModel

/**
 * Main navigation for the application

 */
@Serializable
object Home

/**
 * Represents the recording screen.

 */
@Serializable
object Recording

/**
 * Represents the video playback screen.
 *
 * @property videoId The ID of the video to be played back.

 */
@Serializable
data class VideoPlayback(val videoId: Long)

/**
 * Represents the settings screen.

 */
@Serializable
object Settings

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()

            HomeView(
                previousRecordings = homeViewModel.getPreviousRecordings().collectAsState().value,
                onSettingsSelected = {
                    navController.navigate(Settings)
                },
                onRecordingSelected = { previousRecordingItem ->
                    navController.navigate(VideoPlayback(previousRecordingItem.videoRepositoryId))
                },
                onBeginRecordingSelected = {
                    navController.navigate(Recording)
                }
            )
        }
        composable<Recording> {
            val recordingViewModel: RecordingViewModel = hiltViewModel<RecordingViewModel>()
            val availableCameras = recordingViewModel.availableCameras.collectAsState().value

            RecordingScreenView(
                cameraController = recordingViewModel.lifecycleCameraController,
                availableCameras = availableCameras,
                onCameraSelected = { cameraInfo ->
                    recordingViewModel.cameraDeviceSelected(cameraInfo)
                },
                videoRecordingStateFlow = recordingViewModel.getVideoRecordingState(),
                onStartRecording = {
                    recordingViewModel.startRecording()
                },
                onStopRecording = {
                    recordingViewModel.stopRecording()
                },
                onBackSelected = {
                    navController.popBackStack()
                }
            )
        }
        composable<VideoPlayback> {
            val videoPlaybackViewModel: VideoPlaybackViewModel =
                hiltViewModel<VideoPlaybackViewModel>()

            val videoDetails: VideoPlayback = it.toRoute()

            videoPlaybackViewModel.setVideoId(videoDetails.videoId)

            val videoPlaybackUiState =
                videoPlaybackViewModel.getVideoPlaybackUiState().collectAsState().value

            VideoPlaybackView(
                videoPlaybackUiState = videoPlaybackUiState,
                onBackSelected = {
                    navController.popBackStack()
                },
                onVideoDeletionSelected = { recordingEntity ->
                    navController.popBackStack()
                    videoPlaybackViewModel.deleteVideoRecording(recordingEntity)
                }

            )
        }
        composable<Settings> {
            val applicationSettingsViewModel: ApplicationSettingsViewModel =
                hiltViewModel<ApplicationSettingsViewModel>()
            val applicationSettings =
                applicationSettingsViewModel.getApplicationSettings().collectAsState().value
            ApplicationSettingsView(
                applicationSettings = applicationSettings,
                onSettingsUpdated = { updatedSettings ->
                    applicationSettingsViewModel.updateApplicationSettings(updatedSettings)
                },
                onBackSelected = {
                    navController.popBackStack()
                })
        }
    }
}