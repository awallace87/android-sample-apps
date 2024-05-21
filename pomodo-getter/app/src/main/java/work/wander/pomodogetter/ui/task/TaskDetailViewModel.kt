package work.wander.pomodogetter.ui.task

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import work.wander.pomodogetter.data.tasks.TaskDataRepository
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity
import work.wander.pomodogetter.framework.annotation.BackgroundThread
import work.wander.pomodogetter.framework.logging.AppLogger
import java.time.LocalDate
import javax.inject.Inject

sealed interface TaskDetailUiState {
    object Initial : TaskDetailUiState
    data class Loading(val taskId: Long) : TaskDetailUiState
    data class TaskDataLoaded(val taskDetail: TaskDataEntity) : TaskDetailUiState
    object TaskNotFound : TaskDetailUiState
}

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskDataRepository: TaskDataRepository,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState.Initial)

    val uiState : StateFlow<TaskDetailUiState> = _uiState

    fun setTaskId(taskId: Long) {
        _uiState.value = TaskDetailUiState.Loading(taskId)
        viewModelScope.launch(backgroundDispatcher) {
            val taskDataEntity = taskDataRepository.getTaskById(taskId)
            if (taskDataEntity != null) {
                _uiState.value = TaskDetailUiState.TaskDataLoaded(taskDataEntity)
            } else {
                _uiState.value = TaskDetailUiState.TaskNotFound
            }
        }
    }

    fun onTaskNameChanged(taskName: String) {
        if (_uiState.value is TaskDetailUiState.TaskDataLoaded) {
            val taskDataEntity = (_uiState.value as TaskDetailUiState.TaskDataLoaded).taskDetail
            val updatedTaskDataEntity = taskDataEntity.copy(name = taskName)
            viewModelScope.launch(backgroundDispatcher) {
                taskDataRepository.updateTask(updatedTaskDataEntity)
                _uiState.value = TaskDetailUiState.TaskDataLoaded(updatedTaskDataEntity)
            }
        } else {
            appLogger.warn("Task name changed from invalid state: ${_uiState.value}")
        }
    }

    fun onDueDateChanged(dueDate: LocalDate?) {
        if (_uiState.value is TaskDetailUiState.TaskDataLoaded) {
            val taskDataEntity = (_uiState.value as TaskDetailUiState.TaskDataLoaded).taskDetail
            val updatedTaskDataEntity = taskDataEntity.copy(dueDate = dueDate)
            viewModelScope.launch(backgroundDispatcher) {
                taskDataRepository.updateTask(updatedTaskDataEntity)
                // TODO: Should handle this update with connected flows instead
                _uiState.value = TaskDetailUiState.TaskDataLoaded(updatedTaskDataEntity)
            }
        } else {
            appLogger.warn("Due date changed from invalid state: ${_uiState.value}")
        }
    }

    fun onTaskDeleteSelected() {
        if (_uiState.value is TaskDetailUiState.TaskDataLoaded) {
            val taskDataEntity = (_uiState.value as TaskDetailUiState.TaskDataLoaded).taskDetail
            viewModelScope.launch(backgroundDispatcher) {
                taskDataRepository.deleteTaskById(taskDataEntity.taskId)
            }
        } else {
            appLogger.warn("Task delete selected from invalid state: ${_uiState.value}")
        }
    }
}
