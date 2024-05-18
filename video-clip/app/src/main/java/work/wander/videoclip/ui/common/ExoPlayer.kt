package work.wander.videoclip.ui.common

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerVideoView(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )
    }
}

