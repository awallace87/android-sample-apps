package work.wander.pomodogetter.ui.pomodoro

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import work.wander.pomodogetter.ui.theme.AppTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun PomodoroTimerView(
    uiState: PomodoroTimerUiState,
    modifier: Modifier = Modifier,
    onTimerReady: () -> Unit = {},
    onTimerStart: () -> Unit = {},
    onTimerPause: () -> Unit = {},
    onTimerResume: () -> Unit = {},
    onTimerCancel: () -> Unit = {},
    onTimerDurationChange: (Duration) -> Unit = {},
) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val backPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onTimerCancel()

                isEnabled = false
                backPressedDispatcher?.onBackPressed()
            }
        }
    }

    DisposableEffect(backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backPressedCallback)
        onDispose {
            backPressedCallback.remove()
        }
    }

    Scaffold(
        modifier = modifier,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                when (uiState) {
                    is PomodoroTimerUiState.Initial -> {
                        InitialTimerView(
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }

                    is PomodoroTimerUiState.Running -> {
                        RunningTimerView(
                            uiState = uiState,
                            modifier = Modifier.fillMaxSize(),
                            timerPaused = onTimerPause,
                            onTimerCancel = onTimerCancel,
                        )
                    }

                    is PomodoroTimerUiState.Paused -> {
                        PausedTimerView(
                            uiState,
                            Modifier.fillMaxSize(),
                            onTimerResume,
                            onTimerCancel
                        )
                    }

                    is PomodoroTimerUiState.Completed -> CompletedTimerView(
                        uiState,
                        Modifier.fillMaxSize(),
                        onTimerReady
                    )

                    is PomodoroTimerUiState.Ready -> ReadyTimerView(
                        uiState,
                        Modifier.fillMaxSize(),
                        onTimerStart,
                        onDurationChange = { duration ->
                            onTimerDurationChange(duration)
                        })
                }
            }
        }
    )
}


@Composable
fun ReadyTimerView(
    uiState: PomodoroTimerUiState.Ready,
    modifier: Modifier,
    onTimerStart: () -> Unit = {},
    onDurationChange: (Duration) -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        TimerNotificationPermissionDisplay(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
                .fillMaxWidth(0.7f),
        )
        val infiniteTransition = rememberInfiniteTransition(
            label = "infiniteTransition"
        )
        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "angleTransition"
        )

        val brush: Brush = Brush.sweepGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.tertiary,
            ),
        )

        Box(
            modifier = Modifier
                .size(320.dp)
                .rotate(angle)
                .border(brush = brush, shape = CircleShape, width = 24.dp)
        )

        val duration = uiState.initialDuration

        if (uiState.boundTask != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TimerDisplay(
                        millisRemaining = uiState.boundTask.taskDuration.inWholeMilliseconds,
                        // TODO change to a style parameter instead
                        isRunning = true,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onTimerStart() },
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = "Task: ${uiState.boundTask.taskName}",
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        } else {
            TimerDurationSelector(
                initialDuration = duration,
                onDurationChange = onDurationChange,
                onTimerStart = onTimerStart,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(200.dp)
            )
        }
    }
}

@Composable
fun CompletedTimerView(
    uiState: PomodoroTimerUiState.Completed,
    modifier: Modifier = Modifier,
    onTimerReady: () -> Unit = {}
) {
    // TODO add a celebratory animation
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Completed Timer Screen")
            Text(text = "Total Time: ${uiState.totalDuration.inWholeMinutes} minutes")
            Spacer(modifier = Modifier.size(16.dp))
            Button(onClick = onTimerReady) {
                Text(text = "Ready")
            }
            if (uiState.boundTask != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = "Task: ${uiState.boundTask.taskName}",
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

        }
    }
}

@Composable
fun PausedTimerView(
    uiState: PomodoroTimerUiState.Paused,
    modifier: Modifier = Modifier,
    onTimerResume: () -> Unit = {},
    onTimerCancel: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        TimerNotificationPermissionDisplay(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
                .fillMaxWidth(0.7f),
        )
        IconButton(
            onClick = { onTimerCancel() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Timer",
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
            )
        }
        TimerDisplay(
            millisRemaining = uiState.remainingDuration.inWholeMilliseconds,
            modifier = Modifier
                .clickable { onTimerResume() }
                .align(Alignment.Center),
            isRunning = false,
        )
        TimerProgressIndicator(
            millisRemaining = uiState.remainingDuration.inWholeMilliseconds,
            millisTotal = uiState.totalDuration.inWholeMilliseconds,
            modifier = Modifier.size(320.dp),
            isRunning = false,
        )
        if (uiState.boundTask != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Task: ${uiState.boundTask.taskName}",
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun RunningTimerView(
    uiState: PomodoroTimerUiState.Running,
    modifier: Modifier = Modifier,
    timerPaused: () -> Unit = {},
    onTimerCancel: () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        TimerNotificationPermissionDisplay(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
                .fillMaxWidth(0.7f),
        )
        IconButton(
            onClick = { onTimerCancel() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Timer",
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
            )
        }
        TimerDisplay(
            millisRemaining = uiState.remainingDuration.inWholeMilliseconds,
            modifier = Modifier
                .clickable { timerPaused() }
                .align(Alignment.Center),
            isRunning = true,
        )
        TimerProgressIndicator(
            millisRemaining = uiState.remainingDuration.inWholeMilliseconds,
            millisTotal = uiState.totalDuration.inWholeMilliseconds,
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.Center),
            isRunning = true,
        )
        if (uiState.boundTask != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Task: ${uiState.boundTask.taskName}",
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

    }
}

@Composable
fun InitialTimerView(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Initial Timer Screen")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimerNotificationPermissionDisplay(
    modifier: Modifier = Modifier,
) {
    val foregroundNotificationPermissions =
        rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)

    if (foregroundNotificationPermissions.hasPermission) {
        return
    }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Enable Push Notifications to receive timer updates.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedButton(onClick = { foregroundNotificationPermissions.launchPermissionRequest() }) {
                Text(
                    "Request Permission",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}


@Composable
fun TimerDisplay(
    millisRemaining: Long,
    modifier: Modifier = Modifier,
    isRunning: Boolean = false,
) {
    val minutes = (millisRemaining / 1000) / 60
    val seconds = (millisRemaining / 1000) % 60
    val formattedSeconds = String.format("%02d", seconds)

    Text(
        text = "$minutes:$formattedSeconds",
        fontSize = 48.sp,
        style = MaterialTheme.typography.displayMedium,
        modifier = modifier,
        color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
    )
}

@Composable
fun TimerProgressIndicator(
    millisRemaining: Long,
    millisTotal: Long,
    modifier: Modifier = Modifier,
    isRunning: Boolean = false,
) {
    CircularProgressIndicator(
        progress = (millisRemaining / millisTotal.toFloat()),
        modifier = modifier,
        strokeWidth = 24.dp,
        color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    )
}

@Composable
fun TimerDurationSelector(
    initialDuration: Duration,
    onDurationChange: (Duration) -> Unit,
    onTimerStart: () -> Unit,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
) {
    val userEditingDuration = remember {
        mutableStateOf(isEditing)
    }
    val duration = remember {
        mutableStateOf(initialDuration)
    }


    val minuteDurationRange = IntRange(2, 60)

    Column(
        modifier = modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userEditingDuration.value) {
            Text(
                text = "${duration.value.inWholeMinutes} minutes",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.size(4.dp))
            Slider(
                value = duration.value.inWholeMinutes.toFloat(),
                onValueChange = { minutes ->
                    duration.value = minutes.toLong().minutes
                },
                steps = minuteDurationRange.count(),
                valueRange = minuteDurationRange.first.toFloat()..minuteDurationRange.last.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(4.dp))
            OutlinedButton(
                onClick = {
                    userEditingDuration.value = false
                    onDurationChange(duration.value)
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Done",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        } else {

            Text(
                text = "Tap To Start",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTimerStart() },
            )
            Spacer(modifier = Modifier.size(4.dp))

            val seconds = duration.value.inWholeSeconds % 60
            val formattedSeconds = String.format("%02d", seconds)
            Text(
                text = "${duration.value.inWholeMinutes}:$formattedSeconds",
                fontSize = 48.sp,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTimerStart() },
            )
            Spacer(modifier = Modifier.size(4.dp))
            OutlinedButton(
                onClick = {
                    userEditingDuration.value = true
                },
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Edit",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

        }
    }
}

// TODO create previews for other states
@Composable
@Preview(showBackground = true)
fun PomodoroTimerViewPreview() {
    AppTheme {
        PomodoroTimerView(
            uiState = PomodoroTimerUiState.Initial,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun RunningTimerViewPreview() {
    AppTheme {
        RunningTimerView(
            uiState = PomodoroTimerUiState.Running(
                totalDuration = 25.minutes,
                remainingDuration = 16.minutes,
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PausedTimerViewPreview() {
    AppTheme {
        PausedTimerView(
            uiState = PomodoroTimerUiState.Paused(
                totalDuration = 25.minutes,
                remainingDuration = 16.minutes,
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun CompletedTimerViewPreview() {
    AppTheme {
        CompletedTimerView(
            uiState = PomodoroTimerUiState.Completed(
                totalDuration = 25.minutes,
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ReadyTimerViewPreview() {
    AppTheme {
        ReadyTimerView(
            uiState = PomodoroTimerUiState.Ready(
                initialDuration = 25.minutes,
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TimerDisplayPreview() {
    AppTheme {
        TimerDisplay(
            millisRemaining = 25.minutes.inWholeMilliseconds,
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TimerProgressIndicatorPreview() {
    AppTheme {
        TimerProgressIndicator(
            millisRemaining = 16.minutes.inWholeMilliseconds,
            millisTotal = 25.minutes.inWholeMilliseconds,
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TimerIndicatorIsRunningPreview() {
    AppTheme {
        TimerProgressIndicator(
            millisRemaining = 16.minutes.inWholeMilliseconds,
            millisTotal = 25.minutes.inWholeMilliseconds,
            modifier = Modifier.size(200.dp),
            isRunning = true
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TimerDurationSelectorPreview() {
    AppTheme {
        TimerDurationSelector(
            initialDuration = 25.minutes,
            onDurationChange = {},
            onTimerStart = {},
            modifier = Modifier.size(200.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TimerDurationSelectorEditingPreview() {
    AppTheme {
        TimerDurationSelector(
            initialDuration = 25.minutes,
            onDurationChange = {},
            onTimerStart = {},
            modifier = Modifier.size(200.dp),
            isEditing = true,
        )
    }
}