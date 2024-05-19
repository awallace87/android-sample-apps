package work.wander.pomogogetter.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview

/**
 * Composable function to display an infinite loading animation.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param contentDescription The content description for the loading indicator.
 */
@Composable
fun LoadingIndicatorView(
    modifier: Modifier = Modifier,
    contentDescription: String = "Loading",
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
            }
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun FullScreenLoadingScreenPreview() {
    LoadingIndicatorView()
}