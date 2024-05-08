package work.wander.directory.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import work.wander.directory.data.settings.ApplicationSettingsRepository
import work.wander.directory.proto.settings.ApplicationSettings
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class ApplicationSettingsViewModel @Inject constructor(
    private val applicationSettingsRepository: ApplicationSettingsRepository
) : ViewModel() {

    private val applicationSettings: StateFlow<ApplicationSettings> =
        applicationSettingsRepository.getApplicationSettings()
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = Duration.ofSeconds(1).toMillis(),
                ), ApplicationSettings.getDefaultInstance()
            )

    fun getApplicationSettings() = applicationSettings

    fun updateApplicationSettings(applicationSettings: ApplicationSettings) {
        viewModelScope.launch {
            applicationSettingsRepository.updateApplicationSettings(applicationSettings)
        }
    }
}