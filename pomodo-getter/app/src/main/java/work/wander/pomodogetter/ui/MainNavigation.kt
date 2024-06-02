package work.wander.pomodogetter.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import work.wander.pomodogetter.ui.home.HomeView
import work.wander.pomodogetter.ui.home.HomeViewModel
import work.wander.pomodogetter.ui.pomodoro.PomodoroTimerView
import work.wander.pomodogetter.ui.pomodoro.PomodoroTimerViewModel
import work.wander.pomodogetter.ui.settings.ApplicationSettingsView
import work.wander.pomodogetter.ui.settings.ApplicationSettingsViewModel
import work.wander.pomodogetter.ui.task.TaskDetailView
import work.wander.pomodogetter.ui.task.TaskDetailViewModel

@Serializable
object Home

@Serializable
object Settings

@Serializable
data class PomodoroTimer(
    val boundTimedTaskId: Long = UNBOUND_TASK_ID
) {
    val hasTaskId: Boolean
        get() = boundTimedTaskId != UNBOUND_TASK_ID

    companion object {
        const val UNBOUND_TASK_ID = -7L
    }
}

@Serializable
data class TaskDetail(val taskId: Long)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
            val tasks = homeViewModel.tasks.collectAsState().value
            val timedTasks = homeViewModel.timedTasks.collectAsState().value
            HomeView(
                tasks = tasks,
                timedTasks = timedTasks,
                modifier = Modifier.fillMaxSize(),
                onNewTaskAdded = { taskName ->
                    homeViewModel.addNewTask(taskName)
                },
                onTaskCompletionChanged = { taskModel, taskCompletion ->
                    homeViewModel.toggleTaskCompletion(taskModel.id, taskCompletion)
                },
                onTaskSelected = { taskModel ->
                    navController.navigate(TaskDetail(taskModel.id))
                },
                onNewTimedTaskAdded = { taskName, duration ->
                    homeViewModel.addNewTimedTask(taskName, duration)
                },
                onTimedTaskSelected = {
                    navController.navigate(PomodoroTimer(it.id))
                },
                onStartPomodoroSelected = {
                    navController.navigate(PomodoroTimer())
                },
                onSettingsSelected = {
                    navController.navigate(Settings)
                }
            )
        }
        composable<Settings> {
            val applicationSettingsViewModel: ApplicationSettingsViewModel =
                hiltViewModel<ApplicationSettingsViewModel>()
            val applicationSettings =
                applicationSettingsViewModel.getApplicationSettings().collectAsState().value
            ApplicationSettingsView(
                applicationSettings = applicationSettings,
                onSettingsUpdated = { updatedSettings ->
                    applicationSettingsViewModel.updateApplicationSettings(updatedSettings)
                },
                onBackSelected = {
                    navController.popBackStack()
                })
        }
        composable<TaskDetail> { backStackEntry ->
            val taskDetail: TaskDetail = backStackEntry.toRoute()
            val taskDetailViewModel: TaskDetailViewModel = hiltViewModel<TaskDetailViewModel>()
            taskDetailViewModel.setTaskId(taskDetail.taskId)
            TaskDetailView(
                taskDetailUiState = taskDetailViewModel.uiState.collectAsState().value,
                modifier = Modifier.fillMaxSize(),
                onBackClicked = {
                    navController.popBackStack()
                },
                onTaskDueDateSet = { dueDate ->
                    taskDetailViewModel.onDueDateChanged(dueDate)
                },
                onTaskNameChanged = { taskName ->
                    taskDetailViewModel.onTaskNameChanged(taskName)
                },
                onTaskDeleteClicked = {
                    taskDetailViewModel.onTaskDeleteSelected()
                    navController.popBackStack()
                }
            )
        }
        composable<PomodoroTimer> { backStackEntry ->
            val pomodoroTimerDetails: PomodoroTimer = backStackEntry.toRoute()
            val pomodoroTimerViewModel: PomodoroTimerViewModel =
                hiltViewModel<PomodoroTimerViewModel>()

            // Remember if setTimedTaskId has been called
            val timedTaskIdSet = remember(pomodoroTimerDetails.boundTimedTaskId) { mutableStateOf(false) }

            // Only call setTimedTaskId if it hasn't been called yet for this taskId
            if (!timedTaskIdSet.value) {
                if (pomodoroTimerDetails.hasTaskId) {
                    pomodoroTimerViewModel.setTimedTaskId(pomodoroTimerDetails.boundTimedTaskId)
                }
                timedTaskIdSet.value = true
            }

            val uiState = pomodoroTimerViewModel.uiState.collectAsState().value

            PomodoroTimerView(
                uiState = uiState,
                modifier = Modifier.fillMaxSize(),
                onTimerDurationChange = { duration ->
                    pomodoroTimerViewModel.setInitialDuration(duration)
                },
                onTimerReady = {
                    pomodoroTimerViewModel.onTimerReady()
                },
                onTimerPause = {
                    pomodoroTimerViewModel.pauseTimer()
                },
                onTimerResume = {
                    pomodoroTimerViewModel.resumeTimer()
                },
                onTimerStart = {
                    pomodoroTimerViewModel.startTimer()
                },
                onTimerCancel = {
                    pomodoroTimerViewModel.cancelTimer()
                },
            )
        }
    }
}