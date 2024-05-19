package work.wander.videoclip.ui.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity
import work.wander.videoclip.ui.common.ExoPlayerVideoView
import work.wander.videoclip.ui.theme.AppTheme

@Composable
fun VideoPlaybackView(
    videoPlaybackUiState: VideoPlaybackUiState,
    modifier: Modifier = Modifier,
    onVideoDeletionSelected: (VideoRecordingEntity) -> Unit = {},
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
            VideoPlaybackContents(videoPlaybackUiState, modifier = Modifier.fillMaxSize(), onVideoDeletionSelected = {
                onVideoDeletionSelected(it)
            })
        }
    }
}

@Composable
fun VideoPlaybackContents(
    videoPlaybackUiState: VideoPlaybackUiState,
    modifier: Modifier = Modifier,
    onVideoDeletionSelected: (VideoRecordingEntity) -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (videoPlaybackUiState) {
            is VideoPlaybackUiState.Error -> {
                Text(
                    text = videoPlaybackUiState.errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
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
                        .padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            is VideoPlaybackUiState.PlayerReady -> {
                ExoPlayerVideoView(
                    exoPlayer = videoPlaybackUiState.exoPlayer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.2f)
                ) {
                    val deleteVideoIsConfirming = remember { mutableStateOf(false) }
                    if (deleteVideoIsConfirming.value) {
                        OutlinedButton(
                            onClick = {
                                onVideoDeletionSelected(videoPlaybackUiState.videoRecordingEntity)
                                deleteVideoIsConfirming.value = false
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = "Confirm Video Deletion"
                            )
                            Text("Confirm Deletion")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { deleteVideoIsConfirming.value = true },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                        ) {
                            Icon(
                                Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete Video"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Video")
                        }
                    }

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
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
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

@Preview
@Composable
private fun VideoPlaybackPreview() {
    AppTheme {
        VideoPlaybackView(
            videoPlaybackUiState = VideoPlaybackUiState.Initial
        )
    }
}