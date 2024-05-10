package work.wander.directory.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import work.wander.directory.ui.directory.DirectoryScreenView
import work.wander.directory.ui.directory.DirectoryScreenViewModel
import work.wander.directory.ui.employee.EmployeeScreenView
import work.wander.directory.ui.employee.EmployeeScreenViewModel
import work.wander.directory.ui.settings.ApplicationSettingsView
import work.wander.directory.ui.settings.ApplicationSettingsViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "directory") {
        composable("directory") {
            val directoryScreenViewModel = hiltViewModel<DirectoryScreenViewModel>()

            val uiState = directoryScreenViewModel.uiState.collectAsState()
            DirectoryScreenView(
                uiState = uiState.value,
                onRefreshRequested = { directoryScreenViewModel.fetchEmployees() },
                isRefreshingData = directoryScreenViewModel.isRefreshing,
                onEmployeeSelected = {
                    navController.navigate("employee/$it")
                },
                refreshOnStart = true,
                onSettingsSelected = { navController.navigate("settings") }
            )
        }
        composable("employee/{employeeId}") { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
            val employeeScreenViewModel = hiltViewModel<EmployeeScreenViewModel>()

            employeeScreenViewModel.setEmployeeId(employeeId)

            val uiState = employeeScreenViewModel.uiState.collectAsState()

            EmployeeScreenView(
                uiState = uiState.value,
                onSettingsSelected = { navController.navigate("settings") },
                onBackSelected = { navController.popBackStack() },
            )
        }
        composable("settings") {
            val applicationSettingsViewModel = hiltViewModel<ApplicationSettingsViewModel>()
            val applicationSettings =
                applicationSettingsViewModel.getApplicationSettings().collectAsState()
            ApplicationSettingsView(
                applicationSettings = applicationSettings.value,
                modifier = Modifier.fillMaxSize(),
                onSettingsUpdated = { applicationSettingsViewModel.updateApplicationSettings(it) },
                onBackSelected = { navController.popBackStack() },
            )
        }
    }
}