package work.wander.pomogogetter.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File

/**
 * `CoilLocalImageView` is a composable function that displays an image from a local file path using the Coil image loading library.
 *
 * TODO: Add a default/fallback image
 *
 * @param filePath The path to the image file to be displayed.
 * @param modifier The modifier to be applied to the image. Default is `Modifier`.
 * @param contentDescription The content description for the image. This is used for accessibility purposes. Default is "Image View".
 */
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