package work.wander.videoclip.ui.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import work.wander.videoclip.ui.common.ExoPlayerVideoView

@Composable
fun VideoPlaybackView(
    videoPlaybackUiState: VideoPlaybackUiState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            VideoPlaybackTopAppBar(onBackSelected = onBackSelected)
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (videoPlaybackUiState) {
                is VideoPlaybackUiState.Error -> {
                    Text(
                        text = videoPlaybackUiState.errorMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                VideoPlaybackUiState.Initial -> {
                    Text(
                        text = "No playback details available.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                }

                is VideoPlaybackUiState.LoadingMedia -> {
                    Text(
                        text = videoPlaybackUiState.loadingMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                is VideoPlaybackUiState.PlayerReady -> {
                    ExoPlayerVideoView(
                        exoPlayer = videoPlaybackUiState.exoPlayer,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlaybackTopAppBar(
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(text = "Video Playback") },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { onBackSelected() }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Navigate Back")
            }
        }
    )
}