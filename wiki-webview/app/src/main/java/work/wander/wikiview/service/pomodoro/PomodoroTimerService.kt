package work.wander.wikiview.service.pomodoro

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import work.wander.wikiview.data.pomodoro.PomodoroDatabase
import work.wander.wikiview.data.pomodoro.entity.CompletedPomodoro
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.framework.time.TimerManager
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@AndroidEntryPoint
class PomodoroTimerService : LifecycleService() {

    @Inject lateinit var logger: AppLogger

    @Inject lateinit var notificationManager: NotificationManager

    @Inject lateinit var timerManager: TimerManager

    // TODO use repository instead of direct database access
    @Inject lateinit var pomodoroDatabase: PomodoroDatabase

    override fun onCreate() {
        super.onCreate()
        logger.debug("PomodoroTimerService.onCreate")
        createAndRegisterNotificationChannel()

        lifecycleScope.launch {
            timerManager.getTimerState(TIMER_MANAGER_KEY).collect { timerState ->
                logger.debug("PomodoroTimerService.onCreate: timerState=$timerState")
                // Update notification view
                onTimerStateUpdate(timerState)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logger.debug("PomodoroTimerService.onStartCommand")
        intent?.let {
            when (it.action) {
                ACTION_START -> {
                    logger.debug("PomodoroTimerService.onStartCommand: ACTION_START")
                    startTimerService()
                }
                ACTION_PAUSE -> {
                    logger.debug("PomodoroTimerService.onStartCommand: ACTION_PAUSE")
                    pauseTimerService()
                }
                ACTION_RESUME -> {
                    logger.debug("PomodoroTimerService.onStartCommand: ACTION_RESUME")
                    resumeTimerService()
                }
                ACTION_STOP -> {
                    logger.debug("PomodoroTimerService.onStartCommand: ACTION_STOP")
                    stopTimerService()
                }
                ACTION_RESET -> {
                    logger.debug("PomodoroTimerService.onStartCommand: ACTION_RESET")
                    // TODO handle default from config
                    val durationMillis =
                        it.getLongExtra(EXTRA_DURATION_MILLIS, 25.minutes.inWholeMilliseconds)
                    resetTimerService(durationMillis.milliseconds)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onTimerStateUpdate(timerState: TimerManager.TimerState) {
        when (timerState) {
            is TimerManager.TimerState.Running -> {
                startForeground(NOTIFICATION_ID, createNotification(timerState))
            }
            is TimerManager.TimerState.Paused -> {
                startForeground(NOTIFICATION_ID, createNotification(timerState))
            }
            is TimerManager.TimerState.Completed -> {
                startForeground(NOTIFICATION_ID, createNotification(timerState))
                logCompletedPomodoro(timerState)
            }
            else -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }
    }

    private fun logCompletedPomodoro(timerState: TimerManager.TimerState.Completed) {
        lifecycleScope.launch {
            val completedPomodoro = CompletedPomodoro(
                duration = timerState.totalDuration,
                startedAt = timerState.startedAt,
                completedAt = Instant.now()
            )
            pomodoroDatabase.completedPomodoroDao().insertCompletedPomodoro(completedPomodoro)
        }

    }

    private fun resetTimerService(duration: Duration) {
        timerManager.resetTimer(TIMER_MANAGER_KEY, duration)
    }

    private fun stopTimerService() {
        timerManager.stopTimer(TIMER_MANAGER_KEY)
    }

    private fun startTimerService() {
        timerManager.startTimer(TIMER_MANAGER_KEY)
    }

    private fun pauseTimerService() {
        timerManager.pauseTimer(TIMER_MANAGER_KEY)
    }

    private fun resumeTimerService() {
        timerManager.resumeTimer(TIMER_MANAGER_KEY)
    }

    private fun createNotification(timerState: TimerManager.TimerState) : Notification {
        val title = when (timerState) {
            is TimerManager.TimerState.Running -> "Running"
            is TimerManager.TimerState.Paused -> "Paused"
            is TimerManager.TimerState.Completed -> "Completed"
            else -> "Unknown"
        }
        val content = when (timerState) {
            is TimerManager.TimerState.Running -> formatDurationForNotification(timerState.remainingDuration)
            is TimerManager.TimerState.Paused -> formatDurationForNotification(timerState.remainingDuration)
            is TimerManager.TimerState.Completed -> "Completed"
            else -> ""
        }
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Pomodoro Timer - $title")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)

        if (timerState is TimerManager.TimerState.Running) {
            builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Pause",
                    Launcher(this).pauseTimerIntent()
                )
            )
        } else if (timerState is TimerManager.TimerState.Paused) {
            builder.addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_play,
                    "Resume",
                    Launcher(this).resumeTimerIntent()
                )
            )
        }

        return builder.build()
    }

    private fun formatDurationForNotification(duration: Duration): String {
        val minutes = duration.inWholeMinutes
        val seconds = duration.inWholeSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun createAndRegisterNotificationChannel() {
        val name = NOTIFICATION_CHANNEL_NAME
        val descriptionText = "Pomodoro Timer"
        // Setting to Low to avoid incessant sound and vibration
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        // TODO hide values as internal or private as needed
        const val NOTIFICATION_ID = 1
        const val ACTION_RESET = "work.wander.pomodogetter.service.pomodoro.RESET"
        const val ACTION_START = "work.wander.pomodogetter.service.pomodoro.START"
        const val ACTION_PAUSE = "work.wander.pomodogetter.service.pomodoro.PAUSE"
        const val ACTION_RESUME = "work.wander.pomodogetter.service.pomodoro.RESUME"
        const val ACTION_STOP = "work.wander.pomodogetter.service.pomodoro.STOP"

        const val EXTRA_DURATION_MILLIS = "work.wander.pomodogetter.service.pomodoro.EXTRA_DURATION_MILLIS"

        const val NOTIFICATION_CHANNEL_ID = "PomodoroTimerServiceChannel"
        const val NOTIFICATION_CHANNEL_NAME = "Pomodoro Timer"

        const val TIMER_MANAGER_KEY = "pomodoro_timer_service"

    }

    class Launcher(
        private val context: Context
    ) {
        fun resetTimer(duration: Duration) {
            val intent = Intent(context, PomodoroTimerService::class.java)
            intent.action = ACTION_RESET
            intent.putExtra(EXTRA_DURATION_MILLIS, duration.inWholeMilliseconds)
            context.startService(intent)
        }
        fun startTimer() {
            val intent = Intent(context, PomodoroTimerService::class.java)
            intent.action = ACTION_START
            context.startService(intent)
        }

        fun pauseTimer() {
            val intent = Intent(context, PomodoroTimerService::class.java)
            intent.action = ACTION_PAUSE
            context.startService(intent)
        }

        fun resumeTimer() {
            val intent = Intent(context, PomodoroTimerService::class.java)
            intent.action = ACTION_RESUME
            context.startService(intent)
        }

        fun stopTimer() {
            val intent = Intent(context, PomodoroTimerService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }

        fun pauseTimerIntent(): PendingIntent {
            val intent = Intent(context, PomodoroTimerService::class.java)
            intent.action = ACTION_PAUSE
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        fun resumeTimerIntent(): PendingIntent {
            val intent = Intent(context, PomodoroTimerService::class.java)
            intent.action = ACTION_RESUME
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
    }
}