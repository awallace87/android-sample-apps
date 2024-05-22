package work.wander.pomodogetter.framework.time

import android.os.CountDownTimer
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import work.wander.pomodogetter.framework.logging.AppLogger
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toDuration

interface TimerManager {
    fun resetTimer(
        key: String,
        duration: Duration,
        millisPerTick: Long = TimeUnit.SECONDS.toMillis(1)
    )

    fun startTimer(
        key: String,
    )

    fun pauseTimer(
        key: String
    )

    fun resumeTimer(
        key: String
    )

    fun stopTimer(
        key: String
    )

    fun getTimerState(key: String): StateFlow<TimerState>

    sealed class TimerState {
        object Uninitialized : TimerState()

        data class Ready(
            val initialDuration: Duration,
            val millisPerTick: Long,
        ) : TimerState()

        data class Running(
            val startedAt: Instant,
            val remainingDuration: Duration,
            val totalDuration: Duration,
            val millisPerTick: Long,
        ) : TimerState()

        data class Paused(
            val startedAt: Instant,
            val remainingDuration: Duration,
            val totalDuration: Duration,
            val millisPerTick: Long,
        ) : TimerState()

        data class Completed(
            val startedAt: Instant,
            val totalDuration: Duration
        ) : TimerState()

    }

}

class SavedTimer @AssistedInject constructor(
    private val logger: AppLogger,
) {
    private val _stateFlow =
        MutableStateFlow<TimerManager.TimerState>(TimerManager.TimerState.Uninitialized)
    val stateFlow: StateFlow<TimerManager.TimerState> = _stateFlow

    private var countDownTimer: CountDownTimer? = null

    fun start() {
        _stateFlow.update {
            if (it is TimerManager.TimerState.Ready) {
                countDownTimer?.start()
                TimerManager.TimerState.Running(
                    startedAt = Instant.now(),
                    remainingDuration = it.initialDuration,
                    totalDuration = it.initialDuration,
                    millisPerTick = it.millisPerTick)
            } else {
                logger.warn("Timer not in ready state (state: $it)")
                it
            }
        }
    }

    fun pause() {
        _stateFlow.update {
            if (it is TimerManager.TimerState.Running) {
                countDownTimer?.cancel()
                TimerManager.TimerState.Paused(
                    it.startedAt,
                    it.remainingDuration,
                    it.totalDuration,
                    it.millisPerTick
                )
            } else {
                logger.warn("Timer not in running state (state: $it)")
                it
            }
        }
    }

    fun resume() {
        _stateFlow.update {
            if (it is TimerManager.TimerState.Paused) {
                countDownTimer = createCountDownTimer(it.remainingDuration, it.totalDuration, it.millisPerTick)
                countDownTimer?.start()
                TimerManager.TimerState.Running(
                    it.startedAt,
                    it.remainingDuration,
                    it.totalDuration,
                    it.millisPerTick
                )
            } else {
                logger.warn("Timer not in paused state (state: $it)")
                it
            }
        }
    }

    fun stop() {
        _stateFlow.update {
            if (it is TimerManager.TimerState.Running || it is TimerManager.TimerState.Paused) {
                countDownTimer?.cancel()
                val remainingDuration = when (it) {
                    is TimerManager.TimerState.Running -> it.totalDuration
                    is TimerManager.TimerState.Paused -> it.totalDuration
                    else -> 25.minutes
                }
                val millisPerTick = when (it) {
                    is TimerManager.TimerState.Running -> it.millisPerTick
                    is TimerManager.TimerState.Paused -> it.millisPerTick
                    else -> TimeUnit.SECONDS.toMillis(1)
                }

                TimerManager.TimerState.Ready(
                    remainingDuration,
                    millisPerTick
                )
            } else {
                logger.warn("Unable to stop: Timer not in running state (state: $it)")
                it
            }
        }
    }

    fun reset(
        totalDuration: Duration,
        millisPerTick: Long = TimeUnit.SECONDS.toMillis(1)
    ) {
        _stateFlow.update {
            // TODO log warning if not in completed/uninitialized state
            countDownTimer?.cancel()
            countDownTimer = createCountDownTimer(totalDuration, totalDuration, millisPerTick)
            TimerManager.TimerState.Ready(totalDuration, millisPerTick)
        }
    }

    private fun createCountDownTimer(countdownDuration: Duration, totalDuration: Duration, millisPerTick: Long) =
        object : CountDownTimer(countdownDuration.inWholeMilliseconds, millisPerTick) {

            override fun onTick(millisUntilFinished: Long) {
                _stateFlow.update {
                    val startedAt = when(it) {
                        is TimerManager.TimerState.Running -> it.startedAt
                        is TimerManager.TimerState.Paused -> it.startedAt
                        is TimerManager.TimerState.Completed -> it.startedAt
                        is TimerManager.TimerState.Ready,
                        TimerManager.TimerState.Uninitialized -> {
                            logger.warn("Unexpected state for timer update: $it - Defaulting to estimated timer start time")
                            val elapsedDurationMillis = totalDuration.inWholeMilliseconds - millisUntilFinished
                            Instant.now().minusMillis(elapsedDurationMillis)
                        }
                    }
                    val remainingDuration = millisUntilFinished.milliseconds
                    TimerManager.TimerState.Running(startedAt, remainingDuration, totalDuration, millisPerTick)
                }
            }

            override fun onFinish() {
                _stateFlow.update {
                    val startedAt = when(it) {
                        is TimerManager.TimerState.Running -> it.startedAt
                        is TimerManager.TimerState.Paused -> it.startedAt
                        is TimerManager.TimerState.Completed -> it.startedAt
                        is TimerManager.TimerState.Ready,
                        TimerManager.TimerState.Uninitialized -> {
                            logger.warn("Unexpected state for timer update: $it - Defaulting to estimated timer start time")
                            Instant.now().minusMillis(totalDuration.inWholeMilliseconds)
                        }
                    }
                    TimerManager.TimerState.Completed(startedAt, countdownDuration)
                }
            }
        }
}

@AssistedFactory
interface TimerFactory {
    fun create(): SavedTimer
}

class DefaultTimerManager @Inject constructor(
    private val logger: AppLogger,
    private val timerFactory: TimerFactory,
) : TimerManager {
    private val timers = mutableMapOf<String, SavedTimer>()

    override fun resetTimer(
        key: String,
        duration: Duration,
        millisPerTick: Long
    ) {
        timers.getOrPut(key) {
            timerFactory.create()
        }.reset(duration, millisPerTick)
    }

    override fun startTimer(key: String) {
        val timer = timers.getOrPut(key) {
            timerFactory.create()
        }
        timer.start()
    }

    override fun pauseTimer(key: String) {
        val timer = timers[key]
        if (timer != null) {
            timer.pause()
        } else {
            logger.error("Timer not found for key: $key")
        }
    }

    override fun resumeTimer(key: String) {
        val timer = timers[key]
        if (timer != null) {
            timer.resume()
        } else {
            logger.error("Timer not found for key: $key")
        }
    }

    override fun stopTimer(key: String) {
        val timer = timers[key]
        if (timer != null) {
            timer.stop()
        } else {
            logger.error("Timer not found for key: $key")
        }
    }

    override fun getTimerState(key: String): StateFlow<TimerManager.TimerState> =
        timers.getOrPut(key) {
            timerFactory.create()
        }.stateFlow

}