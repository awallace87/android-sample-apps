package work.wander.pomodogetter.data.pomodoro.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Duration

@Entity(tableName = "in_progress_pomodoros")
data class InProgressPomodoro(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,
    val durationCompleted: Duration,
    val durationTotal: Duration,
)
