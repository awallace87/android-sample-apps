package work.wander.pomodogetter.data.pomodoro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import work.wander.pomodogetter.data.pomodoro.entity.InProgressPomodoro

@Dao
interface InProgressPomodoroDao {

    @Query("SELECT * FROM in_progress_pomodoros")
    fun getInProgressPomodoro(): Flow<List<InProgressPomodoro>>

    @Query("SELECT * FROM in_progress_pomodoros WHERE id = :id")
    suspend fun getInProgressPomodoroById(id: Long): InProgressPomodoro?

    @Insert
    suspend fun insertInProgressPomodoro(inProgressPomodoro: InProgressPomodoro) : Long
}