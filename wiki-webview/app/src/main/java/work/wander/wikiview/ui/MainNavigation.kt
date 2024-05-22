package work.wander.wikiview.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import work.wander.wikiview.ui.home.HomeView
import work.wander.wikiview.ui.home.HomeViewModel
import work.wander.wikiview.ui.pomodoro.PomodoroTimerScreenViewModel
import work.wander.wikiview.ui.pomodoro.PomodoroTimerView
import work.wander.wikiview.ui.settings.ApplicationSettingsView
import work.wander.wikiview.ui.settings.ApplicationSettingsViewModel
import work.wander.wikiview.ui.task.TaskDetailView
import work.wander.wikiview.ui.task.TaskDetailViewModel

@Serializable
object Home

@Serializable
object Settings

@Serializable
object PomodoroTimer

@Serializable
data class TaskDetail(val taskId: Long)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
            val tasks = homeViewModel.tasks.collectAsState().value
            HomeView(
                tasks = tasks,
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
                onStartPomodoroSelected = {
                    navController.navigate(PomodoroTimer)
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
        composable<PomodoroTimer> {
            val pomodoroTimerViewModel: PomodoroTimerScreenViewModel =
                hiltViewModel<PomodoroTimerScreenViewModel>()

            PomodoroTimerView(
                uiState = pomodoroTimerViewModel.uiState.collectAsState().value,
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