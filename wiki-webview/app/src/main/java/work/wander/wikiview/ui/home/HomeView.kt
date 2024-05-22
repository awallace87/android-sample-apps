package work.wander.wikiview.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Top
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import work.wander.wikiview.R
import work.wander.wikiview.ui.theme.AppTheme
import java.time.LocalDate

@Composable
fun HomeView(
    tasks: List<TaskUiModel>,
    modifier: Modifier = Modifier,
    onNewTaskAdded: (String) -> Unit = {},
    onTaskSelected: (TaskUiModel) -> Unit = {},
    onTaskCompletionChanged: (TaskUiModel, Boolean) -> Unit = { _, _ -> },
    onStartPomodoroSelected: () -> Unit = {},
    onSettingsSelected: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopAppBar(onSettingsSelected = onSettingsSelected)
        },
        floatingActionButton = {
            HomeFloatingActionButton(
                onNewTaskAdded = onNewTaskAdded
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HomeViewContents(
                tasks = tasks,
                modifier = Modifier
                    .fillMaxSize(),
                onNewTaskAdded = onNewTaskAdded,
                onTaskSelected = onTaskSelected,
                onTaskCompletionChanged = onTaskCompletionChanged,
                onStartPomodoroSelected = onStartPomodoroSelected,
            )
        }
    }
}

@Composable
fun HomeViewContents(
    tasks: List<TaskUiModel>,
    modifier: Modifier = Modifier,
    onNewTaskAdded: (String) -> Unit = {},
    onTaskSelected: (TaskUiModel) -> Unit = {},
    onTaskCompletionChanged: (TaskUiModel, Boolean) -> Unit = { _, _ -> },
    onStartPomodoroSelected: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Top
    ) {
        HomeViewWelcomeCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        HomePomodoroTimerCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onStartPomodoroSelected = onStartPomodoroSelected,
        )
        HomeViewAddNewTaskCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onNewTaskAdded = onNewTaskAdded,
        )
        HomeViewTaskListCard(
            tasks = tasks,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            onTaskSelected = onTaskSelected,
            onTaskCompletionChanged = onTaskCompletionChanged,
        )
    }
}

@Composable
fun HomeViewWelcomeCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Welcome to PomoDoGetter",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp),
                    //color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Get things done with the Pomodoro Technique, and track your progress with PomoDoGetter!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    )
}

@Composable
fun HomeViewAddNewTaskCard(modifier: Modifier = Modifier, onNewTaskAdded: (String) -> Unit = {}) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.tertiary,
        ),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Is there anything you need to do later?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    //color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                HomeAddNewTaskButton(onNewTaskAdded = onNewTaskAdded)
            }
        })
}

@Composable
fun HomeViewTaskListCard(
    tasks: List<TaskUiModel>,
    modifier: Modifier = Modifier,
    onTaskSelected: (TaskUiModel) -> Unit = {},
    onTaskCompletionChanged: (TaskUiModel, Boolean) -> Unit = { _, _ -> },
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary,
        ),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Your Tasks",
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 4.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks.size) { taskIndex ->
                        val taskModel = tasks[taskIndex]
                        HomeViewTaskListItem(
                            task = taskModel,
                            onTaskSelected = { onTaskSelected(taskModel) },
                            onTaskCompletionChanged = { onTaskCompletionChanged(taskModel, it) }
                        )
                        if (taskIndex < tasks.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(top = 4.dp, start = 24.dp, end = 24.dp),
                                thickness = 2.dp,
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        })
}

@Composable
fun HomeViewTaskListItem(
    task: TaskUiModel,
    modifier: Modifier = Modifier,
    onTaskSelected: () -> Unit = {},
    onTaskCompletionChanged: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTaskSelected() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onTaskCompletionChanged(it) },
            modifier = Modifier.size(40.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.secondary,
            )
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = task.name,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .weight(1f),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
        )
        if (task.dueDate != null) {
            Icon(
                imageVector = Icons.Outlined.Alarm,
                contentDescription = "Due Date",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = task.dueDate.toString(),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun HomeAddNewTaskButton(modifier: Modifier = Modifier, onNewTaskAdded: (String) -> Unit = {}) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        val isAddingNewTask = remember { mutableStateOf(false) }

        if (isAddingNewTask.value) {
            val newTaskName = remember { mutableStateOf("") }
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current

            LaunchedEffect(Unit) {
                delay(100)
                focusRequester.requestFocus()
            }

            OutlinedTextField(
                value = newTaskName.value,
                onValueChange = { newTaskName.value = it },
                modifier = Modifier
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Ascii
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusRequester.freeFocus()
                        onNewTaskAdded(newTaskName.value)
                        isAddingNewTask.value = false
                    }
                )
            )
        } else {
            OutlinedButton(
                onClick = { isAddingNewTask.value = true },
                modifier = Modifier,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
            ) {
                Text(
                    "Add New Task",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onSettingsSelected: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                "PoMoDoGoGetter",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier
            .clip(MaterialTheme.shapes.small),
        colors = TopAppBarDefaults.mediumTopAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "Menu",
                modifier = Modifier
                    .size(32.dp)
                    .padding(start = 4.dp)
                    .clip(MaterialTheme.shapes.small)
            )
        },
        actions = {
            IconButton(onClick = { onSettingsSelected() }) {
                Icon(
                    Icons.Outlined.Settings,
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
    )
}

@Composable
fun HomeFloatingActionButton(
    modifier: Modifier = Modifier,
    onNewTaskAdded: (String) -> Unit = {},
    isEditingNewTaskInitial: Boolean = false,
) {
    val isEditingNewTask = remember { mutableStateOf(isEditingNewTaskInitial) }
    if (isEditingNewTask.value) {
        Row(
            modifier = modifier
                .padding(start = 30.dp, bottom = 16.dp)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primaryContainer),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            val newTaskName = remember { mutableStateOf("") }
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current

            LaunchedEffect(Unit) {
                delay(50)
                focusRequester.requestFocus()
            }

            OutlinedTextField(
                value = newTaskName.value,
                onValueChange = { newTaskName.value = it },
                modifier = Modifier
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    disabledBorderColor = MaterialTheme.colorScheme.tertiary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Ascii
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusRequester.freeFocus()
                        onNewTaskAdded(newTaskName.value)
                        isEditingNewTask.value = false
                    }
                )
            )
            IconButton(
                onClick = {
                    isEditingNewTask.value = false
                    newTaskName.value = ""
                },
                modifier = Modifier.padding(end = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = "Cancel Adding New Task",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    } else {
        FloatingActionButton(
            onClick = { isEditingNewTask.value = true },
            modifier = modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.large),
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add New Task",
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
fun HomePomodoroTimerCard(modifier: Modifier = Modifier, onStartPomodoroSelected: () -> Unit = {}) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary,
        ),
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Ready to get started?",
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 4.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        onStartPomodoroSelected()
                    },
                    modifier = Modifier.padding(bottom = 16.dp),
                ) {
                    Text(
                        "Begin Pomodoro",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }
            }
        }
    )
}

@Preview
@Composable
private fun HomeViewPreview() {
    AppTheme {
        HomeView(
            tasks = listOf(
                TaskUiModel(
                    id = 1,
                    name = "Task 1",
                    isCompleted = false
                ),
                TaskUiModel(
                    id = 2,
                    name = "Task 2",
                    isCompleted = true
                ),
                TaskUiModel(
                    id = 3,
                    name = "Task 3",
                    isCompleted = false
                ),
            )
        )
    }
}

@Preview
@Composable
private fun HomeViewContentsPreview() {
    AppTheme {
        HomeViewContents(
            tasks = listOf(
                TaskUiModel(
                    id = 1,
                    name = "Task 1",
                    isCompleted = false
                ),
                TaskUiModel(
                    id = 2,
                    name = "Task 2",
                    isCompleted = true
                ),
                TaskUiModel(
                    id = 3,
                    name = "Task 3",
                    isCompleted = false
                ),
            )
        )
    }
}

@Preview
@Composable
private fun HomeViewWelcomeCardPreview() {
    AppTheme {
        HomeViewWelcomeCard()
    }
}

@Preview
@Composable
private fun HomeViewAddNewTaskCardPreview() {
    AppTheme {
        HomeViewAddNewTaskCard()
    }
}

@Preview
@Composable
private fun HomeViewTaskListCardPreview() {
    AppTheme {
        HomeViewTaskListCard(
            tasks = listOf(
                TaskUiModel(
                    id = 1,
                    name = "Task 1",
                    isCompleted = false
                ),
                TaskUiModel(
                    id = 2,
                    name = "Task 2",
                    isCompleted = true
                ),
                TaskUiModel(
                    id = 3,
                    name = "Task 3",
                    isCompleted = false
                ),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeViewTaskListItemPreview() {
    AppTheme {
        Column {
            HomeViewTaskListItem(
                task = TaskUiModel(
                    id = 1,
                    name = "Task 1",
                    isCompleted = false
                )
            )
            HomeViewTaskListItem(
                task = TaskUiModel(
                    id = 2,
                    name = "Task 2",
                    isCompleted = true,
                    dueDate = LocalDate.now().plusDays(1)
                )
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun HomeViewTaskListItemDarkPreview() {
    AppTheme(darkTheme = true) {
        Column {
            HomeViewTaskListItem(
                task = TaskUiModel(
                    id = 1,
                    name = "Task 1",
                    isCompleted = false
                )
            )
            HomeViewTaskListItem(
                task = TaskUiModel(
                    id = 2,
                    name = "Task 2",
                    isCompleted = true,
                    dueDate = LocalDate.now().plusDays(1)
                )
            )
        }
    }
}

@Preview
@Composable
private fun HomeAddNewTaskButtonPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            HomeAddNewTaskButton()
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
private fun HomeFloatingActionButtonPreview() {
    AppTheme {
        Column {
            HomeFloatingActionButton(
                modifier = Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HomeFloatingActionButton(
                modifier = Modifier,
                isEditingNewTaskInitial = true
            )
        }
    }
}

@Preview
@Composable
private fun HomeFloatingActionButtonDarkPreview() {
    AppTheme(darkTheme = true) {
        Column {
            HomeFloatingActionButton(
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            HomeFloatingActionButton(
                modifier = Modifier,
                isEditingNewTaskInitial = true
            )
        }
    }
}

@Preview
@Composable
private fun HomePomodoroTimerCardPreview() {
    AppTheme {
        HomePomodoroTimerCard()
    }
}