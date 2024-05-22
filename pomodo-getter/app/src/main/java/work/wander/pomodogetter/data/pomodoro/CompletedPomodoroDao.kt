package work.wander.pomodogetter.data.pomodoro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import work.wander.pomodogetter.data.pomodoro.entity.CompletedPomodoro

@Dao
interface CompletedPomodoroDao {

    @Query("SELECT * FROM completed_pomodoros")
    fun getAllCompletedPomodoros(): Flow<List<CompletedPomodoro>>

    @Insert
    suspend fun insertCompletedPomodoro(completedPomodoro: CompletedPomodoro) : Long
}