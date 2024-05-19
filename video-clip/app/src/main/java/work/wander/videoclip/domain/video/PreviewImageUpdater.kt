package work.wander.videoclip.domain.video

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import work.wander.videoclip.data.recordings.VideoRecordingsRepository
import work.wander.videoclip.domain.image.ImageSaver
import work.wander.videoclip.framework.annotation.BackgroundThread
import work.wander.videoclip.framework.camerax.ForCameraX
import work.wander.videoclip.framework.logging.AppLogger
import javax.inject.Inject

/**
 * Interface for updating the preview image for a video recording, and updating the database with the preview image file path.
 */
interface PreviewImageUpdater {

    /**
     * Creates a preview image for the video file at the given path, and updates the database with the file path.
     */
    fun createAndUpdatePreviewImageFor(videoFilePath: String, recordingId: Long): Deferred<Boolean>
}

/**
 * Default implementation of [PreviewImageUpdater].
 */
class DefaultPreviewImageUpdater @Inject constructor(
    private val videoRecordingRepository: VideoRecordingsRepository,
    @BackgroundThread private val backgroundThreadDispatcher: CoroutineDispatcher,
    @ForCameraX private val backgroundCoroutineScope: CoroutineScope,
    private val imageSaver: ImageSaver,
    private val appLogger: AppLogger,
) : PreviewImageUpdater {

    override fun createAndUpdatePreviewImageFor(
        videoFilePath: String,
        recordingId: Long
    ): Deferred<Boolean> {
        return backgroundCoroutineScope.async(backgroundThreadDispatcher) {
            try {
                val thumbnailBitmap = getBitmapForVideo(videoFilePath)
                if (thumbnailBitmap != null) {
                    val thumbnail = imageSaver.saveThumbnailImage(
                        thumbnailBitmap,
                        "thumbnail_${recordingId}.jpg"
                    ).await()
                    val currentRecording = videoRecordingRepository.getRecordingById(recordingId)
                    if (currentRecording != null) {
                        val updatedRecording = currentRecording.copy(
                            thumbnailFilePath = thumbnail?.absolutePath
                                ?: UNABLE_TO_CREATE_THUMBNAIL
                        )
                        videoRecordingRepository.updateRecording(updatedRecording)
                        true
                    } else {
                        appLogger.error("Failed to update preview image for video, because of entity retrieval issue: ID - $recordingId")
                        false
                    }
                } else {
                    appLogger.error("Failed to create thumbnail for recording: ID - $recordingId")
                    false
                }
            } catch (e: Exception) {
                appLogger.error(
                    e,
                    "Failed to create thumbnail for video: ID - $recordingId"
                )
                false
            }
        }
    }

    // TODO - Allow for custom seek time for frame extraction
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