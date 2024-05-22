package work.wander.pomodogetter.data.pomodoro.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import kotlin.time.Duration

/**
 * Represents a completed pomodoro.
 */
@Entity(tableName = "completed_pomodoros")
data class CompletedPomodoro(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val duration: Duration,
    val startedAt: Instant,
    val completedAt: Instant,
)
