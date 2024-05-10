package work.wander.directory.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import work.wander.directory.data.employee.room.EmployeeDatabase
import work.wander.directory.data.settings.ApplicationSettingsRepository
import work.wander.directory.data.settings.ForApplicationSettingsData
import work.wander.directory.framework.logging.AppLogger
import work.wander.directory.proto.settings.ApplicationSettings
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class ApplicationSettingsViewModel @Inject constructor(
    private val applicationSettingsRepository: ApplicationSettingsRepository,
    @ForApplicationSettingsData private val coroutineDispatcher: CoroutineDispatcher,
    private val employeeDatabase: EmployeeDatabase,
    private val appLogger: AppLogger,
) : ViewModel() {

    private val applicationSettings: StateFlow<ApplicationSettings> =
        applicationSettingsRepository.getApplicationSettings()
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = Duration.ofSeconds(1).toMillis(),
                ), ApplicationSettings.getDefaultInstance()
            )

    private val employeeDatabaseCount = employeeDatabase.employeeDao().getAllEmployees().map { it.count() }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(
            stopTimeoutMillis = Duration.ofSeconds(1).toMillis(),
        ), 0
    )

    fun getApplicationSettings() = applicationSettings

    fun getNumSavedEmployees() = employeeDatabaseCount

    fun updateApplicationSettings(applicationSettings: ApplicationSettings) {
        viewModelScope.launch(coroutineDispatcher) {
            applicationSettingsRepository.updateApplicationSettings(applicationSettings)
        }
    }

    fun clearLocalData() {
        viewModelScope.launch(coroutineDispatcher) {
            employeeDatabase.employeeDao().deleteAllEmployees()
            appLogger.info("Local data cleared")
        }
    }
}

