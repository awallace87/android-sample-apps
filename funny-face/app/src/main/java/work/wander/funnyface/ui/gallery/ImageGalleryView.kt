package work.wander.funnyface.ui.gallery

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ImageGalleryView(
    overlayImages: List<OverlayImage>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = "Image Gallery - ${overlayImages.size} Images",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Alignment.BottomCenter)
        ) {
            items(overlayImages.size) { index ->
                val overlayImage = overlayImages[index]
                ImageGalleryItem(overlayImage = overlayImage, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun ImageGalleryItem(
    overlayImage: OverlayImage,
    modifier: Modifier = Modifier
) {
    val image = BitmapFactory.decodeFile(overlayImage.file.absolutePath)

    Box(
        modifier = modifier
    ) {
        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "Overlay Image",
            modifier = Modifier
                .aspectRatio(image.width.toFloat() / image.height)
        )
    }

}