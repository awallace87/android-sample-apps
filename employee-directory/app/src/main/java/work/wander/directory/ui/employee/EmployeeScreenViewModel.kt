package work.wander.directory.ui.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import work.wander.directory.data.employee.room.EmployeeDatabase
import work.wander.directory.framework.logging.AppLogger
import work.wander.directory.framework.toast.Toaster
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class EmployeeScreenViewModel @Inject constructor(
    employeeDatabase: EmployeeDatabase,
    private val toaster: Toaster,
    private val appLogger: AppLogger,
) : ViewModel() {

    private val _employeeId = MutableStateFlow<String?>(null)

    private val _uiState = _employeeId.combine(employeeDatabase.employeeDao().getAllEmployees()) { employeeId, employees ->
        if (employeeId == null) {
            return@combine EmployeeScreenUiState.Error("Employee not found")
        } else {
            if (employees.isEmpty()) {
                return@combine EmployeeScreenUiState.Loading(employeeId)
            }
            val employee = employees.find { it.id == employeeId }
            if (employee != null) {
                return@combine EmployeeScreenUiState.Success(EmployeeDetailsData.fromEmployeeEntity(employee))
            } else {
                return@combine EmployeeScreenUiState.Error("Employee not found")
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = EmployeeScreenUiState.Initial
    )

    val uiState: StateFlow<EmployeeScreenUiState> = _uiState

    fun setEmployeeId(employeeId: String) {
        _employeeId.value = employeeId
    }

    fun phoneCallRequested(phoneNumber: String) {
        // Handle the phone call request
        appLogger.warn("Phone call requested for $phoneNumber - not implemented")
        toaster.showToast("Share/Call requested for $phoneNumber - not implemented")
    }

    fun emailRequested(emailAddress: String) {
        // Handle the email request
        appLogger.warn("Share/Open $emailAddress - not implemented")
        toaster.showToast("Share/Open $emailAddress - not implemented")
    }
}

sealed class EmployeeScreenUiState {
    data object Initial : EmployeeScreenUiState()
    data class Loading(val employeeId: String) : EmployeeScreenUiState()
    data class Success(val employee: EmployeeDetailsData) : EmployeeScreenUiState()
    data class Error(val message: String) : EmployeeScreenUiState()
}