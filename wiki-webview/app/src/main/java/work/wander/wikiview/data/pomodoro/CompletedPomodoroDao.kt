package work.wander.wikiview.data.pomodoro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import work.wander.wikiview.data.pomodoro.entity.CompletedPomodoro

/**
 * Data access object for completed pomodoros.
 */
@Dao
interface CompletedPomodoroDao {

    /**
     * Returns all completed pomodoros.
     */
    @Query("SELECT * FROM completed_pomodoros")
    fun getAllCompletedPomodoros(): Flow<List<CompletedPomodoro>>

    /**
     * Inserts a completed pomodoro.
     *
     * @param completedPomodoro The completed pomodoro to insert.
     */
    @Insert
    suspend fun insertCompletedPomodoro(completedPomodoro: CompletedPomodoro) : Long
}