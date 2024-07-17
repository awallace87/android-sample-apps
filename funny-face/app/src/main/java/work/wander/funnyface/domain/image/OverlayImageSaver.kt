package work.wander.funnyface.domain.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import work.wander.funnyface.domain.bitmap.rotateBitmap
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
    private var shouldMirror: Boolean = false

    fun setOverlayBitmap(bitmap: Bitmap, shouldMirror: Boolean) {
        latestOverlayBitmap = bitmap
        this.shouldMirror = shouldMirror
    }

    override fun onCaptureSuccess(image: ImageProxy) {
        super.onCaptureSuccess(image)
        val imageBitmap = imageProxyToBitmap(image)
        val rotatedBitmap = rotateBitmap(imageBitmap, image.imageInfo.rotationDegrees.toFloat())
        latestOverlayBitmap?.let { overlayBitmap ->
            val combinedBitmap = combineBitmaps(rotatedBitmap, overlayBitmap)
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
        val resizedOverlay = Bitmap.createScaledBitmap(overlay, background.width, background.height, true)
        val foreground = if (shouldMirror) mirrorOverlayBitmap(resizedOverlay) else resizedOverlay
        val result = Bitmap.createBitmap(background.width, background.height, background.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(background, 0f, 0f, null)
        canvas.drawBitmap(foreground, 0f, 0f, null)
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

    private fun mirrorOverlayBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}