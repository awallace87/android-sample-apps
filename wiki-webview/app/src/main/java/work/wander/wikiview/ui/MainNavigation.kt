package work.wander.wikiview.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import work.wander.wikiview.ui.home.HomeView
import work.wander.wikiview.ui.home.HomeViewModel
import work.wander.wikiview.ui.settings.ApplicationSettingsView
import work.wander.wikiview.ui.settings.ApplicationSettingsViewModel

@Serializable
object Home

@Serializable
object Settings

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>()

            HomeView(
                searchListUiState = homeViewModel.searchUiState(),
                modifier = Modifier.fillMaxSize(),
                onSearchRequested = { query ->
                    homeViewModel.search(query)
                },
                onSearchResultSelected = {
                    homeViewModel.setDetailPanePageTitle(it.key)
                },
                onInternalLinkSelected = {
                    homeViewModel.onInternalLinkSelected(it)
                },
                onSettingsSelected = {
                    navController.navigate(Settings)
                },
                detailUiState = homeViewModel.detailUiState()
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
    }
}