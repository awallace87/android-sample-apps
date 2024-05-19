package work.wander.pomodogetter.ui.common

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Composable function to display an image loaded with Picasso.
 *
 * @param url The URL of the image to load.
 * @param modifier Modifier to be applied to the layout.
 */
@Composable
fun PicassoImage(
    url: String?,
    modifier: Modifier = Modifier,
    displayLoadingAnimation: Boolean = true
) {
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val loadedBitmap = Picasso.get().load(url).get()
                bitmap.value = loadedBitmap
            } catch (e: Exception) {
                // Handle the error
            }
        }
    }

    bitmap.value?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Image loaded with Picasso (URL: $url)",
            modifier = modifier
        )
    } ?: Box(
        modifier = modifier
    ) {
        if (displayLoadingAnimation) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }
    }

}
