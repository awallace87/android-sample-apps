package work.wander.pomodogetter.data.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import work.wander.pomodogetter.data.tasks.entity.TimedTaskDataEntity

/**
 * Data access object for timed tasks.
 */
@Dao
interface TimedTaskDao {

    /**
     * Retrieves all timed tasks from the database.
     *
     * @return Flow emitting list of all timed tasks.
     */
    @Query("SELECT * FROM timed_tasks")
    fun getAllTimedTasks(): Flow<List<TimedTaskDataEntity>>

    /**
     * Inserts a timed task into the database.
     *
     * @param timedTaskDataEntity The timed task to insert.
     * @return The ID of the inserted timed task.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimedTask(timedTaskDataEntity: TimedTaskDataEntity) : Long

    /**
     * Retrieves a timed task from the database by its ID.
     *
     * @param taskId The ID of the timed task to retrieve.
     * @return The task with the given ID, or null if no such task exists.
     */
    @Query("SELECT * FROM timed_tasks WHERE taskId = :taskId")
    suspend fun getTimedTaskById(taskId: Long): TimedTaskDataEntity?

    /**
     * Updates a timed task in the database.
     *
     * @param timedTaskDataEntity The task to update.
     * @return The number of rows affected.
     */
    @Update
    suspend fun updateTimedTask(timedTaskDataEntity: TimedTaskDataEntity) : Int
}