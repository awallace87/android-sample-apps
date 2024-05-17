package work.wander.videoclip.ui.recording

import android.Manifest
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.StateFlow
import work.wander.videoclip.ui.theme.AppTheme

@OptIn(ExperimentalPermissionsApi::class)
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun RecordingScreenView(
    cameraController: LifecycleCameraController,
    videoRecordingStateFlow: StateFlow<VideoRecordingState>,
    modifier: Modifier = Modifier,
    selectedCamera: CameraSelectionInfo = CameraSelectionInfo.Unspecified,
    availableCameras: List<CameraSelectionInfo> = emptyList(),
    onCameraSelected: (CameraSelectionInfo) -> Unit = {},
    onStartRecording: () -> Unit = {},
    onStopRecording: () -> Unit = {},
    onBackSelected: () -> Unit = {},
) {
    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)

    // TODO: Move to viewmodel, or controlled setting, or Effect scope
    val lifecycleOwner = LocalLifecycleOwner.current
    cameraController.bindToLifecycle(lifecycleOwner)

    Box(modifier = modifier.fillMaxSize()) {
        if (cameraPermission.hasPermission) {
            CameraPreview(
                controller = cameraController,
                modifier = Modifier.fillMaxSize(),
            )
            val recordingState = videoRecordingStateFlow.collectAsState().value
            RecordingControls(
                recorderState = recordingState,
                availableCameraSelectors = availableCameras,
                onCameraDeviceSelected = onCameraSelected,
                selectedCamera = selectedCamera,
                onStartRecording = onStartRecording,
                onStopRecording = onStopRecording,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            )
        } else {
            CameraPermissionsDisplay(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                permissionState = cameraPermission,
                onDisplayDismiss = onBackSelected,
            )

        }
    }
}

@Composable
fun RecordingControls(
    modifier: Modifier = Modifier,
    recorderState: VideoRecordingState,
    onCameraDeviceSelected: (CameraSelectionInfo) -> Unit = {},
    availableCameraSelectors: List<CameraSelectionInfo>,
    selectedCamera: CameraSelectionInfo = CameraSelectionInfo.Unspecified,
    onStartRecording: () -> Unit = {},
    onStopRecording: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        RecordingControlButton(
            recorderState = recorderState,
            modifier = Modifier.align(Alignment.Center),
            onStartRecording = onStartRecording,
            onStopRecording = onStopRecording
        )

        val isDeviceSelectionAvailable = when (recorderState) {
            VideoRecordingState.Ready -> true
            VideoRecordingState.Initial,
            VideoRecordingState.Stopped,
            VideoRecordingState.Stopping,
            VideoRecordingState.Starting,
            VideoRecordingState.Recording -> false
        }

        if (isDeviceSelectionAvailable) {
            DeviceSelectionControls(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .align(Alignment.CenterEnd),
                availableCameraDevices = availableCameraSelectors,
                onCameraDeviceSelected = onCameraDeviceSelected
            )
        }
    }
}

@Composable
fun RecordingControlButton(
    recorderState: VideoRecordingState,
    modifier: Modifier = Modifier,
    onStartRecording: () -> Unit = {},
    onStopRecording: () -> Unit = {},
) {
    // TODO: refactor to separate composables
    val primaryColor: Color = when (recorderState) {
        VideoRecordingState.Initial -> MaterialTheme.colorScheme.primary
        VideoRecordingState.Ready -> MaterialTheme.colorScheme.onSecondary
        VideoRecordingState.Starting -> MaterialTheme.colorScheme.onTertiary
        VideoRecordingState.Recording -> MaterialTheme.colorScheme.primary
        VideoRecordingState.Stopping -> MaterialTheme.colorScheme.onSecondary
        VideoRecordingState.Stopped -> MaterialTheme.colorScheme.primary
    }

    val secondaryColor: Color = when (recorderState) {
        VideoRecordingState.Initial -> MaterialTheme.colorScheme.inversePrimary
        VideoRecordingState.Ready -> MaterialTheme.colorScheme.secondary
        VideoRecordingState.Starting -> MaterialTheme.colorScheme.tertiary
        VideoRecordingState.Recording -> MaterialTheme.colorScheme.tertiary
        VideoRecordingState.Stopping -> MaterialTheme.colorScheme.secondary
        VideoRecordingState.Stopped -> MaterialTheme.colorScheme.secondary
    }

    Button(
        modifier = modifier.size(96.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = primaryColor
        ),
        border = BorderStroke(4.dp, secondaryColor),
        onClick = {
            when (recorderState) {
                VideoRecordingState.Ready -> {
                    onStartRecording()
                }
                VideoRecordingState.Initial,
                VideoRecordingState.Stopped,
                VideoRecordingState.Stopping,
                VideoRecordingState.Starting -> {
                    // Do nothing
                }
                VideoRecordingState.Recording -> {
                    onStopRecording()
                }
            }

        },
    ) {
        val iconVector = when (recorderState) {
            VideoRecordingState.Initial -> Icons.Default.VideocamOff
            VideoRecordingState.Ready -> Icons.Default.FiberManualRecord
            VideoRecordingState.Starting -> Icons.Outlined.Camera
            VideoRecordingState.Recording -> Icons.Default.Stop
            VideoRecordingState.Stopping -> Icons.Default.Camera
            VideoRecordingState.Stopped -> Icons.Default.Replay
        }
        Icon(
            iconVector,
            contentDescription = "Start Recording",
            tint = secondaryColor,
            modifier = Modifier
                .aspectRatio(1f)
        )

    }
}

@Composable
fun DeviceSelectionControls(
    modifier: Modifier = Modifier,
    availableCameraDevices: List<CameraSelectionInfo>,
    onCameraDeviceSelected: (CameraSelectionInfo) -> Unit = {},
) {
    val isCameraSelectorDropdownExpanded = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedButton(
            modifier = Modifier.size(72.dp),
            onClick = { isCameraSelectorDropdownExpanded.value = true }
        ) {
            Icon(
                Icons.Outlined.Cameraswitch,
                contentDescription = "Select Camera Device",
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        DropdownMenu(
            expanded = isCameraSelectorDropdownExpanded.value,
            onDismissRequest = { isCameraSelectorDropdownExpanded.value = false }) {
            availableCameraDevices.forEach { cameraSelectionInfo ->
                DropdownMenuItem(
                    text = { Text(cameraSelectionInfo.displayText) },
                    onClick = {
                        onCameraDeviceSelected(cameraSelectionInfo)
                        isCameraSelectorDropdownExpanded.value = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionsDisplay(
    permissionState: PermissionState,
    modifier: Modifier = Modifier,
    onDisplayDismiss: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Granting the camera permission to Video Clip is a requirement to record video clips.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(
            modifier = Modifier.height(8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary,
        )
        if (permissionState.shouldShowRationale) {
            // Display a message explaining why the user should allow the camera permission
            Text(
                text = "The camera is important for this app. Please grant the permission, when prompted.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            HorizontalDivider(
                modifier = Modifier.height(8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary,
            )
            OutlinedButton(
                onClick = { permissionState.launchPermissionRequest() }
            ) {
                Text("Request Permission")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { onDisplayDismiss() }
            ) {
                Text("Dismiss")
            }
        }
    }
}

@Preview
@Composable
private fun CameraRecordingControlButtonPreview() {
    AppTheme {
        Column {
            RecordingControlButton(
                recorderState = VideoRecordingState.Initial,
                onStartRecording = {},
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecordingControlButton(
                recorderState = VideoRecordingState.Ready,
                onStartRecording = {},
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecordingControlButton(
                recorderState = VideoRecordingState.Starting,
                onStartRecording = {},
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecordingControlButton(
                recorderState = VideoRecordingState.Recording,
                onStopRecording = {},
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecordingControlButton(
                recorderState = VideoRecordingState.Stopping,
                onStopRecording = {},
            )
            Spacer(modifier = Modifier.height(8.dp))
            RecordingControlButton(
                recorderState = VideoRecordingState.Stopped,
                onStopRecording = {},
            )
        }
    }
}

@Preview
@Composable
private fun CameraDeviceSelectionControlsPreview() {
AppTheme {
        DeviceSelectionControls(
            availableCameraDevices = listOf(CameraSelectionInfo.Unspecified, CameraSelectionInfo.Unspecified),
            onCameraDeviceSelected = {}
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun CameraPermissionsDisplayPreview() {
    AppTheme {
        val permissionState: PermissionState = object : PermissionState {
            override val hasPermission: Boolean
                get() = false
            override val permission: String
                get() = Manifest.permission.CAMERA
            override val permissionRequested: Boolean
                get() = false
            override val shouldShowRationale: Boolean
                get() = true

            override fun launchPermissionRequest() {
                {}
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            CameraPermissionsDisplay(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                permissionState = permissionState
            )
        }
    }
}

/**
 * This is a Composable function that displays the camera preview.
 *
 * @param controller The controller for the camera lifecycle.
 * @param modifier The modifier to be applied to the CameraPreview.
 */
@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
            }
        },
        modifier = modifier
    )
}

