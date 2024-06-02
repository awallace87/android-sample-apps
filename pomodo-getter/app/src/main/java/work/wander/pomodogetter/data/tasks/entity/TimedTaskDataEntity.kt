package work.wander.pomodogetter.data.tasks.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import kotlin.time.Duration

@Entity(tableName = "timed_tasks")
data class TimedTaskDataEntity(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val name: String,
    val dueDate: LocalDate? = null,
    val initialDuration: Duration,
    val durationRemaining: Duration = initialDuration,
    val createdAt: Instant,
    val firstStartedAt: Instant? = null,
    val completedAt: Instant? = null,
) {
    val isCompleted: Boolean get() = completedAt != null
}
