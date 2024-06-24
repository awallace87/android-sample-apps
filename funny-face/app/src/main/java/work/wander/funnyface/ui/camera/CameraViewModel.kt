package work.wander.funnyface.ui.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import work.wander.funnyface.camera.CameraXManager
import work.wander.funnyface.domain.image.OverlayImageSaver
import work.wander.funnyface.framework.annotation.BackgroundThread
import work.wander.funnyface.framework.logging.AppLogger
import java.util.concurrent.Executor
import javax.inject.Inject

data class CameraDeviceSelectionUiItem(
    val description: String,
    val cameraSelector: CameraSelector?,
    val cameraInfo: CameraInfo?,
) {
    companion object {
        val UNSPECIFIED = CameraDeviceSelectionUiItem(
            description = "Initial Camera",
            cameraSelector = null,
            cameraInfo = null,
        )
    }
}

sealed interface FaceDetectionResult {
    data object NoDetection : FaceDetectionResult

    data class FaceDetected(
        val timestamp: Long,
        val shouldMirror: Boolean = false,
        val faces: List<Face>
    ) : FaceDetectionResult
}

@HiltViewModel
class CameraViewModel @Inject constructor(
    val lifecycleCameraController: LifecycleCameraController,
    private val cameraXManager: CameraXManager,
    @BackgroundThread private val backgroundExecutor: Executor,
    private val overlayImageSaver: OverlayImageSaver,
    private val appLogger: AppLogger,
) : ViewModel() {
    private val faceDetectionOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .enableTracking()
        .setExecutor(backgroundExecutor)

    private val faceDetection = FaceDetection.getClient(faceDetectionOptions.build())

    private val mlKitAnalyzer = MlKitAnalyzer(
        listOf(faceDetection),
        COORDINATE_SYSTEM_VIEW_REFERENCED,
        backgroundExecutor
    ) {
        processDetectionResult(it)
    }

    init {
        val enabledUseCases = LifecycleCameraController.IMAGE_ANALYSIS or LifecycleCameraController.IMAGE_CAPTURE
        lifecycleCameraController.setEnabledUseCases(enabledUseCases)
        lifecycleCameraController.imageAnalysisImageQueueDepth = 10
        lifecycleCameraController.imageAnalysisBackpressureStrategy =
            ImageAnalysis.STRATEGY_BLOCK_PRODUCER

        lifecycleCameraController.setImageAnalysisAnalyzer(backgroundExecutor, mlKitAnalyzer)

        // Image Capture
        lifecycleCameraController.imageCaptureMode = ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
        lifecycleCameraController.imageCaptureFlashMode = ImageCapture.FLASH_MODE_OFF
        lifecycleCameraController.imageCaptureIoExecutor = backgroundExecutor
    }

    private val faceDetectionsFlow =
        MutableStateFlow<FaceDetectionResult>(FaceDetectionResult.NoDetection)

    fun faceDetections(): StateFlow<FaceDetectionResult> {
        return faceDetectionsFlow
    }

    private val selectedCameraDevice: MutableStateFlow<CameraDeviceSelectionUiItem> =
        MutableStateFlow(CameraDeviceSelectionUiItem.UNSPECIFIED)

    val availableCameras: StateFlow<List<CameraDeviceSelectionUiItem>> = flow {
        var camNum = 0
        emit(cameraXManager.getAvailableCameraDevices().await().map {
            CameraDeviceSelectionUiItem(
                description = "Camera $camNum",
                cameraInfo = it,
                cameraSelector = it.cameraSelector,
            ).also {
                camNum++
            }
        })
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun selectCameraDevice(cameraDeviceSelectionUiItem: CameraDeviceSelectionUiItem) {
        appLogger.info("Selected camera device: $cameraDeviceSelectionUiItem")
        cameraDeviceSelectionUiItem.cameraSelector?.let {
            selectedCameraDevice.update { cameraDeviceSelectionUiItem }
            lifecycleCameraController.cameraSelector = it
        }
            ?: appLogger.error("Camera selector is null for camera device: $cameraDeviceSelectionUiItem")
    }

    private val imageCaptureCallback = object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)
            appLogger.info("Image captured successfully")
        }

        override fun onError(exception: ImageCaptureException) {
            super.onError(exception)
            appLogger.error("Error capturing image: ${exception.message}")
        }
    }

    fun captureImage(overlayBitmap: Bitmap) {
        overlayImageSaver.setOverlayBitmap(overlayBitmap)
        lifecycleCameraController.takePicture(
            backgroundExecutor,
            overlayImageSaver
        )
    }

    @SuppressLint("RestrictedApi")
    private fun processDetectionResult(result: MlKitAnalyzer.Result?) {
        if (result == null) {
            faceDetectionsFlow.value = FaceDetectionResult.NoDetection
        } else {
            val resultValue = result.getValue(faceDetection)

            @Suppress("UNCHECKED_CAST")
            val faces: List<Face> =
                resultValue as? List<Face> ?: emptyList()

            val shouldMirror = (selectedCameraDevice.value.cameraSelector?.lensFacing
                ?: LENS_FACING_BACK) == LENS_FACING_FRONT

            faceDetectionsFlow.value = FaceDetectionResult.FaceDetected(
                result.timestamp,
                shouldMirror,
                faces,
            )
        }
    }
}