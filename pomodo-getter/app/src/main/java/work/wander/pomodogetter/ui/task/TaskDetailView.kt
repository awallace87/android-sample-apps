package work.wander.pomodogetter.ui.task

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity
import work.wander.pomodogetter.ui.theme.AppTheme
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit


@Composable
fun TaskDetailView(
    taskDetailUiState: TaskDetailUiState,
    modifier: Modifier = Modifier,
    onTaskDeleteClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TaskDetailTopBar(
                taskUiState = taskDetailUiState,
                onBackClicked = onBackClicked,
                onDeleteClicked = onTaskDeleteClicked,
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (taskDetailUiState) {
                is TaskDetailUiState.Loading -> {
                    Text("Loading...")

                }

                is TaskDetailUiState.TaskDataLoaded -> {
                    TaskContents(
                        taskDataEntity = taskDetailUiState.taskDetail,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                TaskDetailUiState.Initial -> {
                    Text("Initial")
                }

                TaskDetailUiState.TaskNotFound -> {
                    Text("Task not found")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskContents(taskDataEntity: TaskDataEntity, modifier: Modifier = Modifier, displayTaskDueDate: Boolean = false) {
    Column(modifier = modifier) {
        val nameTextFieldValue = remember { mutableStateOf(taskDataEntity.name) }
        OutlinedTextField(
            value = nameTextFieldValue.value,
            onValueChange = {
                nameTextFieldValue.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            textStyle = MaterialTheme.typography.headlineSmall,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
            ),
        )
        val isEditingDueDate = remember { mutableStateOf(displayTaskDueDate) }
        // TODO Move to common UI component
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = taskDataEntity.dueDate?.atStartOfDay()
                ?.toInstant(ZoneOffset.UTC)?.toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val yesterdayInEpochMillis =
                        Instant.now().minusSeconds(TimeUnit.DAYS.toSeconds(1))
                            .toEpochMilli()
                    return utcTimeMillis >= yesterdayInEpochMillis
                }

                override fun isSelectableYear(year: Int): Boolean {
                    val currentYear = Instant.now().atZone(ZoneId.systemDefault()).year
                    return year >= currentYear && year <= currentYear + 3
                }
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            IconButton(onClick = { isEditingDueDate.value = true }) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = "Back",
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            if (taskDataEntity.dueDate != null) {
                Text(text = taskDataEntity.dueDate.toString())
            } else {
                Text(text = "No due date")
            }
        }
        if (isEditingDueDate.value) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailTopBar(
    taskUiState: TaskDetailUiState,
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = { },
    onDeleteClicked: () -> Unit = { },
) {
    val isSettingsDropdownExpanded = remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text("Task Detail") },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = { onBackClicked() }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (taskUiState is TaskDetailUiState.TaskDataLoaded) {
                IconButton(onClick = { isSettingsDropdownExpanded.value = true }) {
                    Icon(Icons.Outlined.MoreVert, contentDescription = "More task options")
                }

                DropdownMenu(
                    expanded = isSettingsDropdownExpanded.value,
                    onDismissRequest = { isSettingsDropdownExpanded.value = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") }, onClick = {
                            onDeleteClicked()
                            isSettingsDropdownExpanded.value = false
                        })
                }
            }
        })
}

@Preview
@Composable
fun TaskDetailTopBarPreview() {
    AppTheme {
        TaskDetailTopBar(
            taskUiState = TaskDetailUiState.TaskDataLoaded(
                TaskDataEntity(
                    taskId = 1,
                    name = "Task 1",
                    isCompleted = false,
                    createdAt = Instant.now(),
                )
            )
        )
    }
}

@Preview
@Composable
fun TaskContentsPreview() {
    AppTheme {
        TaskContents(
            taskDataEntity = TaskDataEntity(
                taskId = 1,
                name = "Task 1",
                isCompleted = false,
                createdAt = Instant.now(),
            )
        )
    }
}

@Preview
@Composable
fun TaskContentsDueDateEditPreview() {
    AppTheme {
        TaskContents(
            taskDataEntity = TaskDataEntity(
                taskId = 1,
                name = "Task 1",
                isCompleted = false,
                createdAt = Instant.now(),
            ),
            displayTaskDueDate = true
        )
    }
}



@Preview
@Composable
fun TaskDetailViewPreview() {
    AppTheme {
        TaskDetailView(
            taskDetailUiState = TaskDetailUiState.TaskDataLoaded(
                TaskDataEntity(
                    taskId = 1,
                    name = "Task 1",
                    isCompleted = false,
                    createdAt = Instant.now(),
                )
            )
        )
    }
}