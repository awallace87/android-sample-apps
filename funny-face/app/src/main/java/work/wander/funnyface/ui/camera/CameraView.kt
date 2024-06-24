package work.wander.funnyface.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Cameraswitch
import androidx.compose.material.icons.outlined.FaceRetouchingOff
import androidx.compose.material.icons.outlined.Image
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.face.FaceLandmark
import work.wander.funnyface.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(
    lifecycleCameraController: LifecycleCameraController,
    availableDevices: List<CameraDeviceSelectionUiItem>,
    onCameraSelected: (CameraDeviceSelectionUiItem) -> Unit,
    latestDetectionResult: State<FaceDetectionResult>,
    onImageCaptureClicked: (Bitmap) -> Unit,
    onGallerySelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cameraPermissions = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    if (!cameraPermissions.hasPermission) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CameraPermissionsDisplay(permissionState = cameraPermissions)
        }
    } else {
        Box(modifier = modifier) {
            val lifecycleOwner = LocalLifecycleOwner.current
            val latestOverlayBitmap = remember { mutableStateOf<Bitmap?>(null) }
            DisposableEffect(lifecycleOwner) {
                lifecycleCameraController.bindToLifecycle(lifecycleOwner)

                onDispose {
                    lifecycleCameraController.unbind()
                }
            }
            CameraXPreview(
                controller = lifecycleCameraController,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .align(Alignment.TopStart)
            )

            FaceDetectionResultOverlay(
                latestDetectionResult = latestDetectionResult,
                onCanvasBitmapCaptured = { bitmap -> latestOverlayBitmap.value = bitmap },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .align(Alignment.TopStart)
            )

            OutlinedButton(shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                onClick = {
                    onGallerySelected()
                }) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = "Navigate to Gallery",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            OutlinedButton(
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                onClick = {
                    latestOverlayBitmap.value?.let { bitmap ->
                        onImageCaptureClicked(bitmap)
                    }
                }) {
                Icon(
                    imageVector = Icons.Outlined.Camera,
                    contentDescription = "Close Camera",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            DeviceSelectionControls(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                availableCameraDevices = availableDevices,
                onCameraDeviceSelected = onCameraSelected
            )
        }
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
    onCanvasBitmapCaptured: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val detectionResult = latestDetectionResult.value
    val context = LocalContext.current
    val originalGlassesBitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.funny_glasses_transparent)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    Box(modifier = modifier) {
        when (detectionResult) {
            is FaceDetectionResult.FaceDetected -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    detectionResult.faces.forEach { face ->
                        val boundingBox = face.boundingBox
                        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                        val rightEar = face.getLandmark(FaceLandmark.RIGHT_EAR)

                        // TODO adjust to work with both camera lens facings. This works with Front Facing Only
                        if (leftEar != null && rightEar != null) {
                            val faceHeight = boundingBox.height().toFloat()
                            val destRectF = if (detectionResult.shouldMirror) {
                                val glassesTopLeft = Offset(
                                    rightEar.position.x,
                                    rightEar.position.y - (faceHeight / 3f)
                                )
                                RectF(
                                    glassesTopLeft.x,
                                    glassesTopLeft.y,
                                    leftEar.position.x,
                                    glassesTopLeft.y + (faceHeight / 1.75f)
                                )
                            } else {
                                val glassesTopLeft = Offset(
                                    leftEar.position.x,
                                    leftEar.position.y - (faceHeight / 3f)
                                )
                                RectF(
                                    glassesTopLeft.x,
                                    glassesTopLeft.y,
                                    rightEar.position.x,
                                    glassesTopLeft.y + (faceHeight / 1.75f)
                                )
                            }

                            drawIntoCanvas { canvas ->
                                canvas.nativeCanvas.drawBitmap(
                                    originalGlassesBitmap,
                                    null,
                                    destRectF,
                                    paint
                                )

                                // Write Overlay to Bitmap for Image Capture
                                val bitmap = Bitmap.createBitmap(
                                    size.width.toInt(),
                                    size.height.toInt(),
                                    Bitmap.Config.ARGB_8888
                                )
                                val bitmapCanvas = android.graphics.Canvas(bitmap)
                                bitmapCanvas.drawBitmap(
                                    originalGlassesBitmap,
                                    null,
                                    destRectF,
                                    paint
                                )
                                onCanvasBitmapCaptured(bitmap)
                            }
                        }
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
                .padding(start = 32.dp, end = 32.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary,

            )
        Text(
            text = "Granting the camera permission to Obscura is a requirement to record video clips.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp, bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (permissionState.shouldShowRationale) {
            // Display a message explaining why the user should allow the camera permission
            Text(
                text = "Accessing the camera is required for Obscura's core functionality. Please grant the permission, when prompted.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp, bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
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
