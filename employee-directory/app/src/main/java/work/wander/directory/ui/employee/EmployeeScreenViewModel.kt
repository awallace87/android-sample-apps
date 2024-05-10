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
import work.wander.directory.data.employee.room.EmployeeEntity
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class EmployeeScreenViewModel @Inject constructor(
    employeeDatabase: EmployeeDatabase,
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
                return@combine EmployeeScreenUiState.Success(employee)
            } else {
                return@combine EmployeeScreenUiState.Error("Employee not found")
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = Duration.ofSeconds(1).toMillis()),
        initialValue = EmployeeScreenUiState.Initial
    )

    val uiState: StateFlow<EmployeeScreenUiState> = _uiState

    fun setEmployeeId(employeeId: String) {
        _employeeId.value = employeeId
    }
}

sealed class EmployeeScreenUiState {
    data object Initial : EmployeeScreenUiState()
    data class Loading(val employeeId: String) : EmployeeScreenUiState()
    data class Success(val employee: EmployeeEntity) : EmployeeScreenUiState()
    data class Error(val message: String) : EmployeeScreenUiState()
}