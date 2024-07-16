package work.wander.funnyface.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
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
                .padding(top = 8.dp)
                .align(Alignment.TopCenter)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Top,
            horizontalArrangement = Arrangement.SpaceEvenly
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
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(72.dp),
            border = ButtonDefaults.outlinedButtonBorder().copy(width = 2.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Export Image",
                modifier = Modifier.size(48.dp)
            )
        }
    }

}