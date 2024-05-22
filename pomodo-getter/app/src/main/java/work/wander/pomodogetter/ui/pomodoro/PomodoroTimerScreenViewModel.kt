package work.wander.pomodogetter.ui.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import work.wander.pomodogetter.framework.logging.AppLogger
import work.wander.pomodogetter.framework.time.TimerManager
import work.wander.pomodogetter.service.pomodoro.PomodoroTimerService
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

sealed class PomodoroTimerUiState {
    object Initial : PomodoroTimerUiState()
    data class Ready(
        val initialDuration: Duration,
    ) : PomodoroTimerUiState()

    data class Running(
        val remainingDuration: Duration,
        val totalDuration: Duration,
    ) : PomodoroTimerUiState()

    data class Paused(
        val remainingDuration: Duration,
        val totalDuration: Duration,
    ) : PomodoroTimerUiState()

    data class Completed(
        val totalDuration: Duration,
    ) : PomodoroTimerUiState()
}

@HiltViewModel
class PomodoroTimerScreenViewModel @Inject constructor(
    timerManager: TimerManager,
    private val pomodoroServiceLauncher: PomodoroTimerService.Launcher,
    private val logger: AppLogger,
) : ViewModel() {

    val uiState: StateFlow<PomodoroTimerUiState> =
        timerManager.getTimerState(PomodoroTimerService.TIMER_MANAGER_KEY)
            .map { timerState ->
                when (timerState) {
                    TimerManager.TimerState.Uninitialized -> PomodoroTimerUiState.Initial
                    is TimerManager.TimerState.Ready -> PomodoroTimerUiState.Ready(timerState.initialDuration)
                    is TimerManager.TimerState.Running -> PomodoroTimerUiState.Running(
                        timerState.remainingDuration,
                        timerState.totalDuration
                    )
                    is TimerManager.TimerState.Paused -> PomodoroTimerUiState.Paused(
                        timerState.remainingDuration,
                        timerState.totalDuration
                    )
                    is TimerManager.TimerState.Completed -> PomodoroTimerUiState.Completed(timerState.totalDuration)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, PomodoroTimerUiState.Initial)

    private var initialDuration = 25.minutes

    fun onTimerReady() {
        pomodoroServiceLauncher.resetTimer(initialDuration)
    }

    fun setInitialDuration(duration: Duration) {
        initialDuration = duration
        pomodoroServiceLauncher.resetTimer(duration)
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
        } else {
            logger.error("Invalid state to cancel timer: $currentState")
        }
    }

    companion object {
        private const val INITIAL_DURATION_KEY = "initial"
    }
}