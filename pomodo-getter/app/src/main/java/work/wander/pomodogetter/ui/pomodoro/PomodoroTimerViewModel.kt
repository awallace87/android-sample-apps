package work.wander.pomodogetter.ui.pomodoro

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import work.wander.pomodogetter.data.tasks.TaskDataRepository
import work.wander.pomodogetter.data.tasks.entity.TimedTaskDataEntity
import work.wander.pomodogetter.framework.annotation.BackgroundThread
import work.wander.pomodogetter.framework.logging.AppLogger
import work.wander.pomodogetter.framework.time.TimerManager
import work.wander.pomodogetter.framework.toast.Toaster
import work.wander.pomodogetter.service.pomodoro.PomodoroTimerService
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Sealed interface representing the different states of the Pomodoro timer.
 */
sealed interface PomodoroTimerUiState {
    /**
     * Represents the initial state of the Pomodoro timer.
     */
    data object Initial : PomodoroTimerUiState

    /**
     * Represents the ready state of the Pomodoro timer.
     *
     * @property boundTask The task bound to the timer, or null if no task is bound.
     * @property initialDuration The initial duration of the timer.
     */
    data class Ready(
        val boundTask: PomodoroBoundTask? = null,
        val initialDuration: Duration,
    ) : PomodoroTimerUiState

    /**
     * Represents the running state of the Pomodoro timer.
     *
     * @property boundTask The task bound to the timer, or null if no task is bound.
     * @property remainingDuration The remaining duration of the timer.
     * @property totalDuration The total duration of the timer.
     */
    data class Running(
        val boundTask: PomodoroBoundTask? = null,
        val remainingDuration: Duration,
        val totalDuration: Duration,
    ) : PomodoroTimerUiState

    /**
     * Represents the paused state of the Pomodoro timer.
     *
     * @property boundTask The task bound to the timer, or null if no task is bound.
     * @property remainingDuration The remaining duration of the timer.
     * @property totalDuration The total duration of the timer.
     */
    data class Paused(
        val boundTask: PomodoroBoundTask? = null,
        val remainingDuration: Duration,
        val totalDuration: Duration,
    ) : PomodoroTimerUiState

    /**
     * Represents the completed state of the Pomodoro timer.
     *
     * @property boundTask The task bound to the timer, or null if no task is bound.
     * @property totalDuration The total duration of the timer.
     */
    data class Completed(
        val boundTask: PomodoroBoundTask? = null,
        val totalDuration: Duration,
    ) : PomodoroTimerUiState
}

/**
 * Data class representing a task bound to the Pomodoro timer.
 *
 * @property taskId The ID of the task.
 * @property taskName The name of the task.
 * @property taskDuration The duration of the task.
 */
data class PomodoroBoundTask(
    val taskId: Long,
    val taskName: String,
    val taskDuration: Duration,
)

@HiltViewModel
class PomodoroTimerViewModel @Inject constructor(
    timerManager: TimerManager,
    private val pomodoroServiceLauncher: PomodoroTimerService.Launcher,
    private val taskDataRepository: TaskDataRepository,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher,
    private val toaster: Toaster,
    private val logger: AppLogger,
) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var boundTask: PomodoroBoundTask? = null

    private var defaultTimerDuration: Duration = 25.minutes

    val uiState: StateFlow<PomodoroTimerUiState> =
        timerManager.getTimerState(PomodoroTimerService.TIMER_MANAGER_KEY)
            .map { timerState ->
                val currentBoundTask = boundTask
                // Unsure if this should be done as part of main UI state flow ¯\(ツ)/¯
                if (currentBoundTask != null) {
                    updateBoundTask(currentBoundTask, timerState)
                }
                when (timerState) {
                    TimerManager.TimerState.Uninitialized -> PomodoroTimerUiState.Initial
                    is TimerManager.TimerState.Ready -> {
                        PomodoroTimerUiState.Ready(
                            currentBoundTask,
                            timerState.initialDuration
                        )
                    }

                    is TimerManager.TimerState.Running -> PomodoroTimerUiState.Running(
                        currentBoundTask,
                        timerState.remainingDuration,
                        timerState.totalDuration
                    )

                    is TimerManager.TimerState.Paused -> PomodoroTimerUiState.Paused(
                        currentBoundTask,
                        timerState.remainingDuration,
                        timerState.totalDuration
                    )

                    is TimerManager.TimerState.Completed -> PomodoroTimerUiState.Completed(
                        currentBoundTask,
                        timerState.totalDuration
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, PomodoroTimerUiState.Initial)


    fun onTimerReady() {
        // Might not be necessary once reset logic is better handled
        val currentTask = boundTask
        if (currentTask == null) {
            pomodoroServiceLauncher.resetTimer(defaultTimerDuration)
        }
    }

    fun setInitialDuration(duration: Duration) {
        val currentTask = boundTask
        if (currentTask != null) {
            logger.warn("Cannot set initial duration for bound task")
        } else {
            pomodoroServiceLauncher.resetTimer(duration)
        }
    }

    fun startTimer() {
        val currentState = uiState.value
        if (currentState is PomodoroTimerUiState.Ready) {
            pomodoroServiceLauncher.startTimer()
        } else {
            logger.error("Invalid state to start timer: $currentState")
        }
    }

    fun pauseTimer() {
        val currentState = uiState.value
        if (currentState is PomodoroTimerUiState.Running) {
            pomodoroServiceLauncher.pauseTimer()
        } else {
            logger.error("Invalid state to pause timer: $currentState")
        }
    }

    fun resumeTimer() {
        val currentState = uiState.value
        if (currentState is PomodoroTimerUiState.Paused) {
            pomodoroServiceLauncher.resumeTimer()
        } else {
            logger.error("Invalid state to resume timer: $currentState")
        }
    }

    fun cancelTimer() {
        val currentState = uiState.value
        if (currentState is PomodoroTimerUiState.Running || currentState is PomodoroTimerUiState.Paused) {
            pomodoroServiceLauncher.stopTimer()
            // Cancel existing task binding (to fix incorrect time in Ready state)
            if (boundTask != null) {
                boundTask = null
            }
            pomodoroServiceLauncher.resetTimer(defaultTimerDuration)
        } else {
            logger.error("Invalid state to cancel timer: $currentState")
        }
    }

    fun setTimedTaskId(taskId: Long) {
        viewModelScope.launch(backgroundDispatcher) {
            val task = taskDataRepository.getTimedTaskById(taskId)
            if (task != null) {
                if (task.isCompleted) {
                    logger.warn("Task is already completed, setting to default timer duration")
                    pomodoroServiceLauncher.resetTimer(defaultTimerDuration)
                    withContext(Dispatchers.Main) {
                        toaster.showToast("Task is already completed. Resetting to default timer duration")
                    }
                } else {
                    boundTask = PomodoroBoundTask(taskId, task.name, task.durationRemaining)
                    pomodoroServiceLauncher.resetTimer(task.durationRemaining)
                }
            } else {
                logger.info("No task found for ID: $taskId, setting to default timer duration")
                pomodoroServiceLauncher.resetTimer(defaultTimerDuration)
            }
        }
    }

    private fun updateBoundTask(
        pomodoroBoundTask: PomodoroBoundTask,
        timerState: TimerManager.TimerState
    ) {
        viewModelScope.launch(backgroundDispatcher) {
            logger.debug("Updating task: $pomodoroBoundTask with state: $timerState")
            val currentTask = taskDataRepository.getTimedTaskById(pomodoroBoundTask.taskId)
            if (currentTask == null) {
                logger.error("No task found for ID: ${pomodoroBoundTask.taskId}")
                return@launch
            }

            val updatedTask: TimedTaskDataEntity? = when (timerState) {
                is TimerManager.TimerState.Running -> {
                    currentTask.copy(durationRemaining = timerState.remainingDuration)

                }

                is TimerManager.TimerState.Paused -> {
                    currentTask.copy(durationRemaining = timerState.remainingDuration)

                }

                is TimerManager.TimerState.Completed -> {
                    currentTask.copy(durationRemaining = ZERO, completedAt = Instant.now())
                }

                else -> {
                    // No need to update task for other states
                    null
                }
            }

            if (updatedTask != null) {
                taskDataRepository.updateTimedTask(updatedTask)
            }

        }
    }

}