package work.wander.pomogogetter.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import work.wander.pomogogetter.ui.demo.RoomDemoView
import work.wander.pomogogetter.ui.demo.RoomDemoViewModel
import work.wander.pomogogetter.ui.settings.ApplicationSettingsView
import work.wander.pomogogetter.ui.settings.ApplicationSettingsViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "room") {
        composable("room") {
            val roomDemoViewModel: RoomDemoViewModel = hiltViewModel<RoomDemoViewModel>()
            val demoEntities = roomDemoViewModel.getAllEntities().collectAsState().value
            RoomDemoView(
                modifier = Modifier.fillMaxSize(),
                demoDataEntities = demoEntities,
                onEntityCreate = { newEntityData ->
                    roomDemoViewModel.addNewEntity(newEntityData)
                },
                onEntityEdit = { updatedId, updatedData ->
                    roomDemoViewModel.updateEntity(updatedId, updatedData)
                },
                onEntityDelete = { entityToDelete ->
                    roomDemoViewModel.deleteEntity(entityToDelete)
                },
                onSettingsSelected = {
                    navController.navigate("settings")
                },
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