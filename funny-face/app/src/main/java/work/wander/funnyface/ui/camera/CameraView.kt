package work.wander.funnyface.ui.camera

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.FaceRetouchingOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraView(
    lifecycleCameraController: LifecycleCameraController,
    availableDevices: List<CameraDeviceSelectionUiItem>,
    onCameraSelected: (CameraDeviceSelectionUiItem) -> Unit,
    latestDetectionResult: State<FaceDetectionResult>,
    modifier: Modifier = Modifier,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        lifecycleCameraController.bindToLifecycle(lifecycleOwner)

        onDispose {
            lifecycleCameraController.unbind()
        }
    }

    Box(modifier = modifier) {
        CameraXPreview(
            controller = lifecycleCameraController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Alignment.TopStart)
        )
        FaceDetectionResultOverlay(
            latestDetectionResult = latestDetectionResult,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Alignment.TopStart)
        )

        DeviceSelectionControls(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            availableCameraDevices = availableDevices,
            onCameraDeviceSelected = onCameraSelected
        )
    }
}

@Composable
fun DeviceSelectionControls(
    modifier: Modifier = Modifier,
    availableCameraDevices: List<CameraDeviceSelectionUiItem>,
    onCameraDeviceSelected: (CameraDeviceSelectionUiItem) -> Unit = {},
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
            offset = DpOffset((-32).dp, (-16).dp),
            onDismissRequest = { isCameraSelectorDropdownExpanded.value = false }) {
            availableCameraDevices.forEach { cameraSelectionInfo ->
                DropdownMenuItem(
                    text = { Text(cameraSelectionInfo.description) },
                    onClick = {
                        onCameraDeviceSelected(cameraSelectionInfo)
                        isCameraSelectorDropdownExpanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun FaceDetectionResultOverlay(
    latestDetectionResult: State<FaceDetectionResult>,
    modifier: Modifier = Modifier,
) {
    val detectionResult = latestDetectionResult.value
    Box(modifier = modifier) {
        when (detectionResult) {
            is FaceDetectionResult.FaceDetected -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    detectionResult.faces.forEach { face ->
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(
                                face.boundingBox.left.toFloat(),
                                face.boundingBox.top.toFloat()
                            ),
                            size = Size(
                                face.boundingBox.width().toFloat(),
                                face.boundingBox.height().toFloat()
                            )
                        )
                    }
                }
            }

            is FaceDetectionResult.NoDetection -> {
                Row(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FaceRetouchingOff,
                        contentDescription = "No face detected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "No face detected",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
