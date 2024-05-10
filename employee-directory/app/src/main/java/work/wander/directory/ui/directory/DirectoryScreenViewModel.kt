package work.wander.directory.ui.directory

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import work.wander.directory.data.employee.remote.ForEmployeeRequest
import work.wander.directory.data.employee.remote.RemoteEmployeeDataSource
import work.wander.directory.data.employee.room.EmployeeDatabase
import work.wander.directory.framework.logging.AppLogger
import javax.inject.Inject

/**
 * ViewModel for the directory screen.
 */
@HiltViewModel
class DirectoryScreenViewModel @Inject constructor(
    private val remoteEmployeeDataSource: RemoteEmployeeDataSource,
    private val employeeDatabase: EmployeeDatabase,
    private val appLogger: AppLogger,
    @ForEmployeeRequest private val coroutineDispatcher: CoroutineDispatcher,
)
: ViewModel() {

    private val _uiState = employeeDatabase.employeeDao().getAllEmployees()
        .map {
            DirectoryScreenUiState.Success(it.map { EmployeeRowData.fromEmployeeEntity(it) })
        }
        .catch { e ->
            DirectoryScreenUiState.Error(e.message ?: "An unexpected error occurred")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = DirectoryScreenUiState.Loading
        )

    val uiState: StateFlow<DirectoryScreenUiState> = _uiState

    private val isRefreshingEmployeeData = mutableStateOf(false)

    val isRefreshing: State<Boolean> = isRefreshingEmployeeData
    fun fetchEmployees() {
        viewModelScope.launch(coroutineDispatcher) {
            isRefreshingEmployeeData.value = true
            val remoteFetchResult = remoteEmployeeDataSource.refreshDataFromRemote()
            if (remoteFetchResult is RemoteEmployeeDataSource.EmployeeDataResponse.Success) {
                // Data was fetched successfully
                // Log the data
                appLogger.info("Fetched employee data: ${remoteFetchResult.employees}")
            } else {
                // Notify the user of the error
                // Log the error
                appLogger.error("Failed to fetch employee data: $remoteFetchResult")

            }
            isRefreshingEmployeeData.value = false
        }
    }


}

/**
 * Sealed class representing the possible UI states for the directory screen.
 */
sealed class DirectoryScreenUiState {
    data object Loading : DirectoryScreenUiState()
    data class Success(val employees: List<EmployeeRowData>) : DirectoryScreenUiState()
    data class Error(val message: String) : DirectoryScreenUiState()
}