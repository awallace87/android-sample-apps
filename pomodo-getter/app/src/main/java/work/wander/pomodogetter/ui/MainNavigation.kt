package work.wander.pomodogetter.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import work.wander.pomodogetter.ui.home.HomeView
import work.wander.pomodogetter.ui.home.HomeViewModel
import work.wander.pomodogetter.ui.settings.ApplicationSettingsView
import work.wander.pomodogetter.ui.settings.ApplicationSettingsViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
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
                onSettingsSelected = {
                    navController.navigate("settings")
                }
            )
        }
        composable("settings") {
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
    }
}