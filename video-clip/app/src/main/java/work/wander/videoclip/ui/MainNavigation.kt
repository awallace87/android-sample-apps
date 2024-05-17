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

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()

            HomeView(
                onSettingsSelected = {
                    navController.navigate("settings")
                },
                onRecordingSelected = { recordingId ->
                    navController.navigate("gallery/$recordingId")
                },
                onBeginRecordingSelected = {
                    navController.navigate("recording")
                }
            )
        }
        composable("recording") {
            val recordingViewModel: RecordingViewModel = hiltViewModel<RecordingViewModel>()
            val availableCameras = recordingViewModel.availableCameras.collectAsState().value
            val selectedCamera = recordingViewModel.selectedCamera.collectAsState().value

            RecordingScreenView(
                cameraController = recordingViewModel.lifecycleCameraController,
                availableCameras = availableCameras,
                onCameraSelected = { cameraInfo ->
                    recordingViewModel.cameraDeviceSelected(cameraInfo)
                },
                selectedCamera = selectedCamera,
                videoRecordingStateFlow = recordingViewModel.getVideoRecordingState(),
                onStartRecording = {
                    recordingViewModel.startRecording()
                },
                onStopRecording = {
                    recordingViewModel.stopRecording()
                },
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