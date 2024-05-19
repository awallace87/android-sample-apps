package work.wander.videoclip.ui.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.wander.videoclip.ui.common.ExoPlayerVideoView
import work.wander.videoclip.ui.theme.AppTheme

@Composable
fun VideoPlaybackView(
    videoPlaybackUiState: VideoPlaybackUiState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            VideoPlaybackTopAppBar(videoPlaybackUiState, onBackSelected = onBackSelected)
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
    videoPlaybackUiState: VideoPlaybackUiState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit = {},
) {
    val titleText = when (videoPlaybackUiState) {
        is VideoPlaybackUiState.Error -> "Error Playing Video"
        VideoPlaybackUiState.Initial -> "Video Playback"
        is VideoPlaybackUiState.LoadingMedia -> "Loading ..."
        is VideoPlaybackUiState.PlayerReady -> videoPlaybackUiState.getTitleText()
    }
    TopAppBar(
        title = {
            Text(
                text = titleText,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { onBackSelected() }) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Navigate Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview
@Composable
private fun VideoPlaybackTopAppBarPreview() {
    AppTheme {
        Column {
            VideoPlaybackTopAppBar(
                videoPlaybackUiState = VideoPlaybackUiState.Initial
            )
            Spacer(modifier = Modifier.height(16.dp))
            VideoPlaybackTopAppBar(
                videoPlaybackUiState = VideoPlaybackUiState.LoadingMedia("Loading media")
            )
            Spacer(modifier = Modifier.height(16.dp))
            VideoPlaybackTopAppBar(
                videoPlaybackUiState = VideoPlaybackUiState.Error("Error message")
            )
        }
    }
}