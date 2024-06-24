package work.wander.funnyface.ui.common

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout


/**
 * Composable function to display an image loaded with Picasso.
 *
 * @param url The URL of the image to load.
 * @param modifier The modifier to apply to the image.
 * @param displayLoadingAnimation Whether to display a loading animation while the image is loading.
 * @param timeoutMillis The timeout in milliseconds for loading the image.
 */
@Composable
fun PicassoImage(
    url: String?,
    modifier: Modifier = Modifier,
    displayLoadingAnimation: Boolean = true,
    timeoutMillis: Long = 5000,
) {
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val displayError = remember { mutableStateOf(false) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                withTimeout(timeoutMillis) {
                    val loadedBitmap = Picasso.get().load(url).get()
                    bitmap.value = loadedBitmap
                }
            } catch (timeout: TimeoutCancellationException) {
                displayError.value = true
            } catch (e: Exception) {
                displayError.value = true
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
        if (displayError.value) {
            Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = "Error loading image (URL: $url)",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            if (displayLoadingAnimation) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
        }
    }

}
