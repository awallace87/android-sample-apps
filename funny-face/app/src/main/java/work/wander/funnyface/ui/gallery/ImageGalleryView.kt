package work.wander.funnyface.ui.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import work.wander.funnyface.ui.common.CoilLocalImageView

@Composable
fun ImageGalleryView(
    overlayImages: List<OverlayImage>,
    onExportImageSelected: (OverlayImage) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = "Image Gallery - ${overlayImages.size} Images",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Alignment.BottomCenter)
        ) {
            items(overlayImages.size) { index ->
                val overlayImage = overlayImages[index]
                ImageGalleryItem(
                    overlayImage = overlayImage,
                    onExportImageSelected = {
                        onExportImageSelected(overlayImage)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ImageGalleryItem(
    overlayImage: OverlayImage,
    onExportImageSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        CoilLocalImageView(
            filePath = overlayImage.file.absolutePath,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .align(Alignment.Center),
            contentDescription = "Funny Image",
        )

        OutlinedButton(
            onClick = onExportImageSelected,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Export Image",
                modifier = Modifier.size(24.dp)
            )

        }
    }

}