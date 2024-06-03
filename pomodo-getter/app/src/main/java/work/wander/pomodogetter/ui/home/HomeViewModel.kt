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
import kotlin.time.Duration

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

    private val timedTasksStateFlow = taskDataRepository.getAllTimedTasks().map { timedTaskEntities ->
        timedTaskEntities.map { timedTaskEntity ->
            TimedTaskUiModel(
                id = timedTaskEntity.taskId,
                name = timedTaskEntity.name,
                initialDuration = timedTaskEntity.initialDuration,
                remainingDuration = timedTaskEntity.durationRemaining,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    val tasks : StateFlow<List<TaskUiModel>> = tasksStateFlow

    val timedTasks: StateFlow<List<TimedTaskUiModel>> = timedTasksStateFlow

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
                taskDataRepository.updateTask(taskEntity.copy(isCompleted = isCompleted))
            }
        }
    }

    fun addNewTimedTask(taskName: String, duration: Duration) {
        viewModelScope.launch(backgroundDispatcher) {
            val newTimedTaskEntity = taskDataRepository.addNewTimedTask(taskName, duration)
            appLogger.debug("New timed task added: $newTimedTaskEntity")
        }
    }
}

/**
 * Data class representing a task in the UI.
 *
 * @property id The unique ID of the task.
 * @property name The name of the task.
 * @property isCompleted Whether the task is completed.
 * @property dueDate The due date of the task, or null if the task does not have a due date.
 */
data class TaskUiModel(
    val id: Long,
    val name: String,
    val isCompleted: Boolean,
    val dueDate: LocalDate? = null,
)

/**
 * Data class representing a timed task in the UI.
 *
 * @property id The unique ID of the task.
 * @property name The name of the task.
 * @property initialDuration The initial duration of the task.
 * @property remainingDuration The remaining duration of the task. Defaults to the initial duration.
 * @property dueDate The due date of the task, or null if the task does not have a due date.
 */
data class TimedTaskUiModel(
    val id: Long,
    val name: String,
    val initialDuration: Duration,
    val remainingDuration: Duration = initialDuration,
    val dueDate: LocalDate? = null,
) {
    /**
     * Checks if the task is completed.
     * A task is considered completed if the remaining duration is zero or less.
     */
    val isCompleted: Boolean
        get() = Duration.ZERO >= remainingDuration

    /**
     * Checks if the task has not started.
     * A task is considered not started if the initial duration equals the remaining duration.
     */
    val hasNotStarted: Boolean
        get() = initialDuration == remainingDuration
}