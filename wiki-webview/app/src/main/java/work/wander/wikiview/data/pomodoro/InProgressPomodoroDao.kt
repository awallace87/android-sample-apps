package work.wander.wikiview.data.pomodoro

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import work.wander.wikiview.data.pomodoro.entity.InProgressPomodoro

/**
 * Data access object for in-progress pomodoros.
 */
@Dao
interface InProgressPomodoroDao {

    /**
     * Returns the in-progress pomodoro.
     */
    @Query("SELECT * FROM in_progress_pomodoros")
    fun getInProgressPomodoro(): Flow<List<InProgressPomodoro>>

    /**
     * Returns the in-progress pomodoro with the specified ID.
     *
     * @param id The ID of the in-progress pomodoro to return.
     */
    @Query("SELECT * FROM in_progress_pomodoros WHERE id = :id")
    suspend fun getInProgressPomodoroById(id: Long): InProgressPomodoro?

    /**
     * Inserts an in-progress pomodoro.
     *
     * @param inProgressPomodoro The in-progress pomodoro to insert.
     */
    @Insert
    suspend fun insertInProgressPomodoro(inProgressPomodoro: InProgressPomodoro) : Long
}