package work.wander.videoclip.framework.camerax

import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import work.wander.videoclip.framework.logging.AppLogger
import javax.inject.Inject

interface CameraXManager {

    fun checkInitializationComplete(timeOutMillis: Long = 1000): Deferred<Boolean>

    fun getAvailableCameraDevices(): Deferred<List<CameraInfo>>
}

class DefaultCameraXManager @Inject constructor(
    private val lifecycleCameraController: LifecycleCameraController,
    private val processCameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    @ForCameraX private val cameraXCoroutineScope: CoroutineScope,
    @ForCameraX private val coroutineDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger,
) : CameraXManager {



    override fun checkInitializationComplete(timeOutMillis: Long): Deferred<Boolean> {
        return cameraXCoroutineScope.async(coroutineDispatcher) {
            val initializationFuture = lifecycleCameraController.initializationFuture
            try {
                appLogger.info("Waiting for CameraX initialization to complete")
                val initializationResult = initializationFuture.get()
                appLogger.info("CameraX initialization complete ($initializationResult)")
                return@async true
            } catch (timeoutException: TimeoutCancellationException) {
                appLogger.error("CameraX initialization timed out")
                return@async false
            }
        }
    }

    override fun getAvailableCameraDevices(): Deferred<List<CameraInfo>> {
        return cameraXCoroutineScope.async(coroutineDispatcher) {
            try {
                val cameraProvider = processCameraProviderFuture.get()
                return@async cameraProvider.availableCameraInfos
            } catch (exception: Exception) {
                appLogger.error(exception, "Error getting available camera devices")
                return@async emptyList()
            }
        }
    }


}