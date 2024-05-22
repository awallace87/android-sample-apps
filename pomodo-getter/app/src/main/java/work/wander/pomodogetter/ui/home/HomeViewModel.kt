package work.wander.pomodogetter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import work.wander.pomodogetter.data.tasks.TaskDataRepository
import work.wander.pomodogetter.framework.annotation.BackgroundThread
import work.wander.pomodogetter.framework.logging.AppLogger
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDataRepository: TaskDataRepository,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher,
    private val appLogger: AppLogger
) : ViewModel() {

    private val tasksStateFlow = taskDataRepository.getAllTasks().map { taskEntities ->
        taskEntities.map { taskEntity ->
            TaskUiModel(
                id = taskEntity.taskId,
                name = taskEntity.name,
                isCompleted = taskEntity.isCompleted
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    val tasks : StateFlow<List<TaskUiModel>> = tasksStateFlow

    fun addNewTask(name: String) {
        viewModelScope.launch(backgroundDispatcher) {
            val newTaskEntity = taskDataRepository.addNewTask(name)
            appLogger.debug("New task added: $newTaskEntity")
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch(backgroundDispatcher) {
            val taskEntity = taskDataRepository.getTaskById(taskId)
            taskEntity?.let {
                taskDataRepository.updateTask(taskEntity.copy(isCompleted = !taskEntity.isCompleted))
            }
        }
    }
}

data class TaskUiModel(
    val id: Long,
    val name: String,
    val isCompleted: Boolean,
    val dueDate: LocalDate? = null,
)