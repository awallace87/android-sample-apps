package work.wander.videoclip.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import work.wander.videoclip.ui.home.HomeView
import work.wander.videoclip.ui.home.HomeViewModel
import work.wander.videoclip.ui.recording.RecordingScreenView
import work.wander.videoclip.ui.recording.RecordingViewModel
import work.wander.videoclip.ui.settings.ApplicationSettingsView
import work.wander.videoclip.ui.settings.ApplicationSettingsViewModel
import work.wander.videoclip.ui.video.VideoPlaybackView
import work.wander.videoclip.ui.video.VideoPlaybackViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()

            HomeView(
                previousRecordings = homeViewModel.getPreviousRecordings().collectAsState().value,
                onSettingsSelected = {
                    navController.navigate("settings")
                },
                onRecordingSelected = { previousRecordingItem ->
                    navController.navigate("video/${previousRecordingItem.videoRepositoryId}")
                },
                onBeginRecordingSelected = {
                    navController.navigate("recording")
                }
            )
        }
        composable("recording") {
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
        composable("video/{videoId}") {
            val videoPlaybackViewModel: VideoPlaybackViewModel =
                hiltViewModel<VideoPlaybackViewModel>()

            val videoId = it.arguments?.getString("videoId")?.toLongOrNull()

            videoPlaybackViewModel.setVideoId(videoId ?: VideoPlaybackViewModel.NO_VIDEO_SPECIFIED)

            val videoPlaybackUiState =
                videoPlaybackViewModel.getVideoPlaybackUiState().collectAsState().value

            VideoPlaybackView(
                videoPlaybackUiState = videoPlaybackUiState,
                onBackSelected = {
                    navController.popBackStack()
                }

            )
        }
        composable("settings") {
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