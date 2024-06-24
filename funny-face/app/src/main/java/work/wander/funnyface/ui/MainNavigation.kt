package work.wander.funnyface.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import work.wander.funnyface.ui.camera.CameraView
import work.wander.funnyface.ui.camera.CameraViewModel
import work.wander.funnyface.ui.settings.ApplicationSettingsView
import work.wander.funnyface.ui.settings.ApplicationSettingsViewModel

@Serializable
object Camera

@Serializable
object Settings

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Camera) {
        composable<Camera> {
            val cameraViewModel: CameraViewModel = hiltViewModel<CameraViewModel>()
            val availableDevices = cameraViewModel.availableCameras.collectAsState().value
            CameraView(
                lifecycleCameraController = cameraViewModel.lifecycleCameraController,
                availableDevices = availableDevices,
                onCameraSelected = { cameraDeviceSelectionUiItem ->
                    cameraViewModel.selectCameraDevice(cameraDeviceSelectionUiItem)
                },
                latestDetectionResult = cameraViewModel.faceDetections().collectAsState(),
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