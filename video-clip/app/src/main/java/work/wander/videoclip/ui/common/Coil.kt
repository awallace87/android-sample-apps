package work.wander.videoclip.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

@Composable
fun CoilLocalImageView(
    filePath: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Image View",) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(File(filePath))
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
    )
}