package work.wander.wikiview.data.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import work.wander.wikiview.data.tasks.entity.TaskDataEntity

/**
 * Data access object for tasks.
 */
@Dao
interface TaskDao {

    /**
     * Returns all tasks.
     */
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskDataEntity>>

    /**
     * Returns the task with the specified ID.
     *
     * @param taskId The ID of the task to return.
     */
    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Long): TaskDataEntity?

    /**
     * Inserts a task.
     *
     * @param taskDataEntity The task to insert.
     *
     * @return The ID of the inserted task.
     */
    @Insert
    suspend fun insertTask(taskDataEntity: TaskDataEntity) : Long

    /**
     * Updates a task.
     *
     * @param taskDataEntity The task to update.
     *
     * @return The number of tasks updated.
     */
    @Update
    suspend fun updateTask(taskDataEntity: TaskDataEntity) : Int

    /**
     * Deletes a task.
     *
     * @param taskId The ID of the task to delete.
     *
     * @return The number of tasks deleted.
     */
    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deleteTaskById(taskId: Long) : Int
}