package work.wander.wikiview.data.tasks.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

/**
 * Represents a task.
 *
 * @property taskId The ID of the task.
 * @property name The name of the task.
 * @property isCompleted Whether the task is completed.
 * @property createdAt The date and time the task was created.
 * @property dueDate The due date of the task.
 */
@Entity(tableName = "tasks")
data class TaskDataEntity(
    @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
    val name: String,
    val isCompleted: Boolean,
    val createdAt: Instant,
    val dueDate: LocalDate? = null,
)
