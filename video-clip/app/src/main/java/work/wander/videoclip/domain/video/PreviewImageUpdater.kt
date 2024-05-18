package work.wander.videoclip.domain.video

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import work.wander.videoclip.data.recordings.VideoRecordingsRepository
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity
import work.wander.videoclip.domain.image.ImageSaver
import work.wander.videoclip.framework.annotation.BackgroundThread
import work.wander.videoclip.framework.camerax.ForCameraX
import work.wander.videoclip.framework.logging.AppLogger
import javax.inject.Inject

interface PreviewImageUpdater {

    fun createAndUpdatePreviewImageFor(videoRecordingEntity: VideoRecordingEntity): Deferred<Boolean>
}

class DefaultPreviewImageUpdater @Inject constructor(
    private val videoRecordingRepository: VideoRecordingsRepository,
    @BackgroundThread private val backgroundThreadDispatcher: CoroutineDispatcher,
    @ForCameraX private val backgroundCoroutineScope: CoroutineScope,
    private val imageSaver: ImageSaver,
    private val appLogger: AppLogger,
) : PreviewImageUpdater {

    override fun createAndUpdatePreviewImageFor(videoRecordingEntity: VideoRecordingEntity): Deferred<Boolean> {
        return backgroundCoroutineScope.async(backgroundThreadDispatcher) {
            try {
                val thumbnailBitmap = getBitmapForVideo(videoRecordingEntity.videoFilePath)
                if (thumbnailBitmap != null) {
                    val thumbnail = imageSaver.saveThumbnailImage(
                        thumbnailBitmap,
                        videoRecordingEntity.id.toString()
                    ).await()
                    videoRecordingRepository.updateRecording(
                        videoRecordingEntity.copy(
                            thumbnailFilePath = thumbnail?.path ?: UNABLE_TO_CREATE_THUMBNAIL
                        )
                    )
                    true
                } else {
                    appLogger.error("Failed to create thumbnail for video: ${videoRecordingEntity.videoFilePath}")
                    false
                }
            } catch (e: Exception) {
                appLogger.error(
                    e,
                    "Failed to create thumbnail for video: ${videoRecordingEntity.videoFilePath}"
                )
                false
            }
        }
    }

    // TODO: Implement method
    private fun getBitmapForVideo(videoFilePath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFilePath)
        val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST)
        retriever.release()
        return bitmap
    }

    companion object {
        private const val UNABLE_TO_CREATE_THUMBNAIL = "unable_to_create_thumbnail"
    }
}