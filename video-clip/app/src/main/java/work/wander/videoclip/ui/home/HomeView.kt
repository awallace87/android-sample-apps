package work.wander.videoclip.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FiberManualRecord
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.NotStarted
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.wander.videoclip.R
import work.wander.videoclip.data.recordings.entity.VideoRecordingEntity
import work.wander.videoclip.ui.common.CoilLocalImageView
import work.wander.videoclip.ui.theme.AppTheme

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
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(48.dp)
                )
            }

        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        modifier = modifier
            .padding(8.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            ),
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
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.elevatedCardElevation(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(
                text = "Welcome to Video Clips!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
            Text(
                text = "Your place to create and share short video clips!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
            OutlinedButton(
                onClick = { onBeginRecordingSelected() },
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary,
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp,
                    brush = SolidColor(MaterialTheme.colorScheme.tertiary)
                )
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Navigate to recording screen",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Create New Video Clip",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Text(
                text = "Get started by recording a new clip or selecting a previous recording below.",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(start = 64.dp, end = 64.dp, top = 8.dp, bottom = 16.dp)
                    .fillMaxWidth()
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.elevatedCardElevation(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
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
                Spacer(modifier = Modifier.padding(4.dp))
                OutlinedButton(
                    onClick = { onBeginRecordingSelected() },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 2.dp,
                        brush = SolidColor(MaterialTheme.colorScheme.tertiary)
                    ),
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = "Navigate to recording screen",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Create New Video Clip",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    items(previousRecordings.size) { recording ->
                        val previousRecordingItem = previousRecordings[recording]
                        PreviousRecordingItemView(
                            previousRecordingItem = previousRecordingItem,
                            modifier = Modifier
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
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = previousRecordingItem.captureStartedFormatted(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
            )
            if (previousRecordingItem.thumbnailPath.isNotEmpty()) {
                CoilLocalImageView(
                    filePath = previousRecordingItem.thumbnailPath,
                    contentDescription = "Video Preview Thumbnail",
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(160.dp)
                        .clip(MaterialTheme.shapes.large)
                        .padding(8.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.image_absent),
                    contentDescription = "Preview Image Not Found",
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .fillMaxWidth(0.95f)
                        .height(160.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Outlined.Timer,
                        contentDescription = "Video Duration",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = previousRecordingItem.durationFormatted(),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Outlined.Storage,
                        contentDescription = "Size on Disk (bytes)",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = previousRecordingItem.formatSizeInMb(),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start,
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    val statusIcon = when (previousRecordingItem.recordingStatus) {
                        VideoRecordingEntity.RecordingStatus.SAVED -> Icons.Outlined.CheckCircle
                        VideoRecordingEntity.RecordingStatus.ERROR -> Icons.Outlined.ErrorOutline
                        VideoRecordingEntity.RecordingStatus.FAILED -> Icons.Outlined.Cancel
                        VideoRecordingEntity.RecordingStatus.INITIAL -> Icons.Outlined.NotStarted
                        VideoRecordingEntity.RecordingStatus.STARTING -> Icons.Outlined.Loop
                        VideoRecordingEntity.RecordingStatus.STARTED -> Icons.Outlined.PlayArrow
                        VideoRecordingEntity.RecordingStatus.PAUSED -> Icons.Outlined.Pause
                        VideoRecordingEntity.RecordingStatus.RESUMED -> Icons.Outlined.Restore
                        VideoRecordingEntity.RecordingStatus.RECORDING -> Icons.Outlined.FiberManualRecord
                    }
                    Icon(
                        statusIcon,
                        contentDescription = "Recording Status",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Using Fixed(2) for current grid layout
@Preview
@Composable
private fun PreviousRecordingItemViewPreview() {
    AppTheme {
        Row(modifier = Modifier.fillMaxWidth()) {
            PreviousRecordingItemView(
                modifier = Modifier.weight(0.5f),
                previousRecordingItem = PreviousRecordingItem(
                    videoRepositoryId = 1,
                    durationInMillis = 1000,
                    sizeInBytes = 1000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.SAVED
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            PreviousRecordingItemView(
                modifier = Modifier.weight(0.5f),
                previousRecordingItem = PreviousRecordingItem(
                    videoRepositoryId = 2,
                    durationInMillis = 2000,
                    sizeInBytes = 2000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.ERROR
                )
            )
        }
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

@Preview
@Composable
private fun HomeViewWithRecordingsPreview() {
    AppTheme {
        HomeView(
            previousRecordings = listOf(
                PreviousRecordingItem(
                    videoRepositoryId = 1,
                    durationInMillis = 1000,
                    sizeInBytes = 1000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.SAVED
                ),
                PreviousRecordingItem(
                    videoRepositoryId = 2,
                    durationInMillis = 2000,
                    sizeInBytes = 2000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.ERROR
                ),
                PreviousRecordingItem(
                    videoRepositoryId = 3,
                    durationInMillis = 3000,
                    sizeInBytes = 3000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.FAILED
                ),
                PreviousRecordingItem(
                    videoRepositoryId = 4,
                    durationInMillis = 4000,
                    sizeInBytes = 4000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.SAVED
                ),
                PreviousRecordingItem(
                    videoRepositoryId = 5,
                    durationInMillis = 5000,
                    sizeInBytes = 5000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.SAVED
                ),
                PreviousRecordingItem(
                    videoRepositoryId = 6,
                    durationInMillis = 6000,
                    sizeInBytes = 6000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.SAVED
                ),
                PreviousRecordingItem(
                    videoRepositoryId = 7,
                    durationInMillis = 7000,
                    sizeInBytes = 7000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.SAVED
                ),
                PreviousRecordingItem(
                    videoRepositoryId = 8,
                    durationInMillis = 8000,
                    sizeInBytes = 8000,
                    captureStartedAtEpochMillis = System.currentTimeMillis(),
                    thumbnailPath = "",
                    videoFilePath = "",
                    recordingStatus = VideoRecordingEntity.RecordingStatus.SAVED
                )
            )
        )
    }
}