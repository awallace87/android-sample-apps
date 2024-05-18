package work.wander.videoclip.domain.image

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import work.wander.videoclip.framework.annotation.BackgroundThread
import work.wander.videoclip.framework.camerax.ForCameraX
import java.io.File
import javax.inject.Inject

interface ImageSaver {

    fun saveThumbnailImage(bitmap: Bitmap, fileName: String): Deferred<File?>
}

class DefaultImageSaver @Inject constructor(
    @BackgroundThread private val backgroundThreadDispatcher: CoroutineDispatcher,
    @ForCameraX private val backgroundCoroutineScope: CoroutineScope,
    @ForThumbnailImages private val thumbnailImageDirectory: File,
) : ImageSaver {

    override fun saveThumbnailImage(bitmap: Bitmap, fileName: String): Deferred<File> {
        return backgroundCoroutineScope.async(backgroundThreadDispatcher) {
            val thumbnailFile = File(thumbnailImageDirectory, fileName).apply {
                if (!exists()) {
                    createNewFile()
                }
            }
            thumbnailFile.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            thumbnailFile
        }
    }
}