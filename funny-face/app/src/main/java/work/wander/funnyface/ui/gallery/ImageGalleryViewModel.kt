package work.wander.funnyface.ui.gallery

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import work.wander.funnyface.domain.image.ForOverlayImages
import work.wander.funnyface.framework.share.ImageSharer
import java.io.File
import javax.inject.Inject

data class OverlayImage(
    val file: File
)

@HiltViewModel
class ImageGalleryViewModel @Inject constructor(
    @ForOverlayImages private val overlayImageOutputDirectory: File,
    private val imageSharer: ImageSharer,
) : ViewModel() {

    val overlayImages = (overlayImageOutputDirectory.listFiles()?.toList() ?: emptyList()).map {
            OverlayImage(it)
        }

    fun onExportImageSelected(overlayImage: OverlayImage) {
        imageSharer.shareLocalJpegImage(overlayImage.file)
    }


}