package work.wander.videoclip.ui.recording

import android.Manifest
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@OptIn(ExperimentalPermissionsApi::class)
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun RecordingScreenView(
    cameraController: LifecycleCameraController,
    videoRecordingStateFlow: StateFlow<VideoRecordingState>,
    modifier: Modifier = Modifier,
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
    onStartRecording: () -> Unit = {},
    onStopRecording: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        RecordingControlButton(
            recorderState = recorderState,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 16.dp),
            onStartRecording = onStartRecording,
            onStopRecording = onStopRecording
        )

        val shouldShowRecordingStatus = when (recorderState) {
            VideoRecordingState.Ready,
            VideoRecordingState.Initial -> false

            VideoRecordingState.Starting,
            is VideoRecordingState.Recording,
            VideoRecordingState.Stopping,
            VideoRecordingState.Stopped -> true
        }

        if (shouldShowRecordingStatus) {
            RecordingStatusDisplay(
                videoRecordingState = recorderState,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .align(Alignment.CenterStart)
            )
        }

        val isDeviceSelectionAvailable = when (recorderState) {
            VideoRecordingState.Ready -> true
            VideoRecordingState.Initial,
            VideoRecordingState.Stopped,
            VideoRecordingState.Stopping,
            VideoRecordingState.Starting,
            is VideoRecordingState.Recording -> false
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
    val primaryColor: Color = when (recorderState) {
        VideoRecordingState.Initial -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        VideoRecordingState.Ready -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        VideoRecordingState.Starting -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        is VideoRecordingState.Recording -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        VideoRecordingState.Stopping -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        VideoRecordingState.Stopped -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    }

    val secondaryColor: Color = when (recorderState) {
        VideoRecordingState.Initial -> MaterialTheme.colorScheme.secondary
        VideoRecordingState.Ready -> MaterialTheme.colorScheme.tertiary
        VideoRecordingState.Starting -> MaterialTheme.colorScheme.secondary
        is VideoRecordingState.Recording -> MaterialTheme.colorScheme.tertiary
        VideoRecordingState.Stopping -> MaterialTheme.colorScheme.secondary
        VideoRecordingState.Stopped -> MaterialTheme.colorScheme.tertiary
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
                VideoRecordingState.Stopped,
                VideoRecordingState.Ready -> {
                    onStartRecording()
                }

                VideoRecordingState.Initial,
                VideoRecordingState.Stopping,
                VideoRecordingState.Starting -> {
                    // Do nothing
                }

                is VideoRecordingState.Recording -> {
                    onStopRecording()
                }
            }

        },
    ) {
        val iconVector = when (recorderState) {
            VideoRecordingState.Initial -> Icons.Default.VideocamOff
            VideoRecordingState.Ready -> Icons.Default.FiberManualRecord
            VideoRecordingState.Starting -> Icons.Outlined.Camera
            is VideoRecordingState.Recording -> Icons.Default.Stop
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
fun RecordingStatusDisplay(
    videoRecordingState: VideoRecordingState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (videoRecordingState) {
                VideoRecordingState.Starting -> {
                    Icon(
                        Icons.Default.Camera,
                        contentDescription = "Starting recording...",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Starting",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                }

                is VideoRecordingState.Recording -> {
                    Icon(
                        Icons.Default.FiberManualRecord,
                        contentDescription = "Recording",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val durationMillis = videoRecordingState.recordingDurationMillis
                    val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
                    val minutes =
                        TimeUnit.MILLISECONDS.toMinutes(durationMillis) % TimeUnit.HOURS.toMinutes(1)
                    val seconds =
                        TimeUnit.MILLISECONDS.toSeconds(durationMillis) % TimeUnit.MINUTES.toSeconds(
                            1
                        )
                    val millis = durationMillis % TimeUnit.SECONDS.toMillis(1)
                    val timestamp =
                        String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis)
                    Text(
                        text = timestamp,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                }

                VideoRecordingState.Stopping -> {
                    Icon(
                        Icons.Default.Camera,
                        contentDescription = "Recording",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stopping",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                }

                VideoRecordingState.Stopped -> {
                    Icon(
                        Icons.Default.Done,
                        contentDescription = "Recording",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Saved Video",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                }

                else -> {
                    // Display Nothing
                }
            }

        }
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
            modifier = Modifier
                .size(72.dp),
            onClick = { isCameraSelectorDropdownExpanded.value = true },
            colors = ButtonDefaults.outlinedButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)

            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
        ) {
            Icon(
                Icons.Outlined.Cameraswitch,
                contentDescription = "Select Camera Device",
                tint = MaterialTheme.colorScheme.secondary,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Camera Permission Required",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.Center)
            )
            IconButton(
                onClick = { onDisplayDismiss() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss Permission Request",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )

            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 36.dp, end = 36.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary,

            )
        Text(
            text = "Granting the camera permission to Video Clip is a requirement to record video clips.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (permissionState.shouldShowRationale) {
            // Display a message explaining why the user should allow the camera permission
            Text(
                text = "Taking videos with the camera is vital for this app. Please grant the permission, when prompted.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        OutlinedButton(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            onClick = { permissionState.launchPermissionRequest() },

            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        ) {
            Text("Request Permission")
        }
    }
}

@Preview
@Composable
private fun RecordingStatusDisplayPreview() {
    AppTheme {
        Column {
            RecordingStatusDisplay(videoRecordingState = VideoRecordingState.Starting)
            Spacer(modifier = Modifier.width(8.dp))
            RecordingStatusDisplay(videoRecordingState = VideoRecordingState.Recording(1000L))
            Spacer(modifier = Modifier.width(8.dp))
            RecordingStatusDisplay(videoRecordingState = VideoRecordingState.Stopping)
            Spacer(modifier = Modifier.width(8.dp))
            RecordingStatusDisplay(videoRecordingState = VideoRecordingState.Stopped)
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
                recorderState = VideoRecordingState.Recording(1000L),
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
            availableCameraDevices = listOf(
                CameraSelectionInfo.Unspecified,
                CameraSelectionInfo.Unspecified
            ),
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
                // Do nothing
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

