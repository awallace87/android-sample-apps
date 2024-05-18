package work.wander.videoclip.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.wander.videoclip.R
import work.wander.videoclip.ui.common.CoilLocalImageView
import work.wander.videoclip.ui.theme.AppTheme
import java.time.Instant
import java.time.format.DateTimeFormatter

@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    previousRecordings: List<PreviousRecordingItem> = emptyList(),
    onSettingsSelected: () -> Unit = {},
    onRecordingSelected: (PreviousRecordingItem) -> Unit = {},
    onBeginRecordingSelected: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            HomeTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                onSettingsSelected = onSettingsSelected
            )
        },
        content = {
            Column(
                modifier = modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                HomeContent(
                    modifier = Modifier.fillMaxSize(),
                    previousRecordings = previousRecordings,
                    onRecordingSelected = onRecordingSelected,
                    onBeginRecordingSelected = onBeginRecordingSelected,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(modifier: Modifier = Modifier, onSettingsSelected: () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(
                text = "Video Clips",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,

            ),
        modifier = modifier,
        actions = {
            IconButton(onClick = { onSettingsSelected() }) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "Navigate to settings",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}


@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    previousRecordings: List<PreviousRecordingItem> = emptyList(),
    onRecordingSelected: (PreviousRecordingItem) -> Unit = {},
    onBeginRecordingSelected: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.cardColors()
        ) {
            Text(
                text = "Welcome to Video Clips!",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
            Text(
                text = "Your place to create and share short video clips!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
            OutlinedButton(
                onClick = { onBeginRecordingSelected() },
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Navigate to recording screen")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Video Clip")
            }
            Text(
                text = "Get started by recording a new clip or selecting a previous recording below.",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.cardColors()
        ) {
            Text(
                text = "Previous Recordings",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
            if (previousRecordings.isEmpty()) {
                Text(
                    text = "No previous recordings found.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(16.dp))
                OutlinedButton(
                    onClick = { onBeginRecordingSelected() },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally),
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Navigate to recording screen")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create New Video Clip")
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(previousRecordings.size) { recording ->
                        val previousRecordingItem = previousRecordings[recording]
                        PreviousRecordingItemView(
                            previousRecordingItem = previousRecordingItem,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { onRecordingSelected(previousRecordingItem) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviousRecordingItemView(
    previousRecordingItem: PreviousRecordingItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = previousRecordingItem.captureStartedFormatted(),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            )
            if (previousRecordingItem.thumbnailPath.isNotEmpty()) {
                CoilLocalImageView(filePath = previousRecordingItem.thumbnailPath,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp))
            } else {
                Image(
                    painter = painterResource(id = R.drawable.image_absent),
                    contentDescription = "Thumbnail",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(0.33f)
                ) {
                    Icon(
                        Icons.Outlined.Timelapse,
                        contentDescription = "Video Duration",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = previousRecordingItem.durationFormatted(),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(0.33f)
                ) {
                    Icon(Icons.Outlined.Storage, contentDescription = "Size on Disk (bytes)")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = previousRecordingItem.formatSizeInMb(),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(0.33f)
                ) {
                    Icon(
                        Icons.Outlined.Camera,
                        contentDescription = "Recording Status",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = previousRecordingItem.recordingStatus,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

            }

        }
    }
}

@Preview
@Composable
private fun PreviousRecordingItemViewPreview() {
    AppTheme {
        PreviousRecordingItemView(
            previousRecordingItem = PreviousRecordingItem(
                videoRepositoryId = 1,
                durationInMillis = 1000,
                sizeInBytes = 1000,
                captureStartedAtEpochMillis = System.currentTimeMillis(),
                thumbnailPath = "",
                videoFilePath = "",
                recordingStatus = "Unspecified"
            )
        )
    }
}

@Preview
@Composable
private fun HomeTopAppBarPreview() {
    AppTheme {
        HomeTopAppBar()
    }
}


@Preview
@Composable
private fun HomeContentPreview() {
    AppTheme {
        HomeContent()
    }
}

@Preview
@Composable
private fun HomeViewPreview() {
    AppTheme {
        HomeView()
    }
}