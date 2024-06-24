package work.wander.funnyface.domain.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import work.wander.funnyface.framework.clock.AppClock
import work.wander.funnyface.framework.logging.AppLogger
import java.io.File
import java.nio.ByteBuffer
import javax.inject.Inject

class OverlayImageSaver @Inject constructor(
    @ForOverlayImages private val outputDirectory: File,
    private val appClock: AppClock,
    private val appLogger: AppLogger,
) : ImageCapture.OnImageCapturedCallback() {

    private var latestOverlayBitmap: Bitmap? = null

    fun setOverlayBitmap(bitmap: Bitmap) {
        latestOverlayBitmap = bitmap
    }

    override fun onCaptureSuccess(image: ImageProxy) {
        super.onCaptureSuccess(image)
        val imageBitmap = imageProxyToBitmap(image)
        latestOverlayBitmap?.let { overlayBitmap ->
            val combinedBitmap = combineBitmaps(imageBitmap, overlayBitmap)
            saveBitmapToFile(combinedBitmap)
            appLogger.debug("Combined bitmap size: ${combinedBitmap.byteCount}")
        } ?: appLogger.error("Overlay bitmap is null, cannot combine images")
    }

    override fun onError(exception: ImageCaptureException) {
        super.onError(exception)
        appLogger.error(exception, "Error capturing image: ${exception.message}")
    }

    // This method assumes that the image is in JPEG format
    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun combineBitmaps(background: Bitmap, overlay: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(background.width, background.height, background.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(background, 0f, 0f, null)
        canvas.drawBitmap(overlay, 0f, 0f, null)
        return result
    }

    private fun saveBitmapToFile(bitmap: Bitmap) {
        try {
            val file = File(outputDirectory, "overlay_${appClock.currentEpochTimeMillis()}.jpg")
            file.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        } catch (e: Exception) {
            appLogger.error(e, "Error saving bitmap to file")
        }
    }
}