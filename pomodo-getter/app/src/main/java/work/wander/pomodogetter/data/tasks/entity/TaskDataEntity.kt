package work.wander.pomodogetter.data.tasks.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TaskDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isCompleted: Boolean,
    val createdAt: Instant,
    val dueDate: LocalDate? = null,
)
