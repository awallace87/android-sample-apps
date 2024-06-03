package work.wander.pomodogetter.data.tasks

import kotlinx.coroutines.flow.Flow
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity
import work.wander.pomodogetter.data.tasks.entity.TimedTaskDataEntity
import work.wander.pomodogetter.framework.logging.AppLogger
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration

/**
 * Repository for task data.
 */
interface TaskDataRepository {

    /**
     * Returns all tasks.
     */
    fun getAllTasks(): Flow<List<TaskDataEntity>>

    /**
     * Adds a new task with the specified name.
     *
     * @param name The name of the task.
     *
     * @return The new task, or null if the task could not be added.
     */
    suspend fun addNewTask(name: String): TaskDataEntity?

    /**
     * Returns the task with the specified ID.
     *
     * @param taskId The ID of the task to return.
     *
     * @return The task with the specified ID, or null if the task could not be found.
     */
    suspend fun getTaskById(taskId: Long): TaskDataEntity?

    /**
     * Updates the specified task.
     *
     * @param taskDataEntity The task to update.
     *
     * @return True if the task was updated, false otherwise.
     */
    suspend fun updateTask(taskDataEntity: TaskDataEntity): Boolean

    /**
     * Deletes the task with the specified ID.
     *
     * @param taskId The ID of the task to delete.
     *
     * @return True if the task was deleted, false otherwise.
     */
    suspend fun deleteTaskById(taskId: Long): Boolean

    fun getAllTimedTasks(): Flow<List<TimedTaskDataEntity>>

    suspend fun addNewTimedTask(name: String, duration: Duration): TimedTaskDataEntity?
    suspend fun getTimedTaskById(taskId: Long): TimedTaskDataEntity?
    suspend fun updateTimedTask(updatedTimedTask: TimedTaskDataEntity) : Boolean

}

/**
 * Default implementation of [TaskDataRepository].

 */
class DefaultTaskDataRepository @Inject constructor(
    private val taskDatabase: TaskDatabase,
    private val appLogger: AppLogger,
) : TaskDataRepository {

    override fun getAllTasks(): Flow<List<TaskDataEntity>> {
        return taskDatabase.taskDao().getAllTasks()
    }

    override suspend fun addNewTask(name: String): TaskDataEntity? {
        val insertEntity =
            TaskDataEntity(name = name, isCompleted = false, createdAt = Instant.now())
        val newEntityId = taskDatabase.taskDao().insertTask(insertEntity)
        return taskDatabase.taskDao().getTaskById(newEntityId)
    }

    override suspend fun getTaskById(taskId: Long): TaskDataEntity? {
        return taskDatabase.taskDao().getTaskById(taskId)
    }

    override suspend fun updateTask(taskDataEntity: TaskDataEntity): Boolean {
        val numRowsUpdated = taskDatabase.taskDao().updateTask(taskDataEntity)
        if (numRowsUpdated > 1) {
            appLogger.warn("More than one row ($numRowsUpdated) updated for task: $taskDataEntity. Unexpected!")
            return false
        } else if (numRowsUpdated == 0) {
            appLogger.warn("No rows updated for task: $taskDataEntity. Task not found!")
            return false
        } else {
            return true
        }
    }

    override suspend fun deleteTaskById(taskId: Long): Boolean {
        val numRowsDeleted = taskDatabase.taskDao().deleteTaskById(taskId)
        if (numRowsDeleted > 1) {
            appLogger.warn("More than one row ($numRowsDeleted) deleted for task: $taskId. Unexpected!")
            return false
        } else if (numRowsDeleted == 0) {
            appLogger.warn("No rows deleted for task: $taskId. Task not found!")
            return false
        } else {
            return true
        }
    }

    override fun getAllTimedTasks(): Flow<List<TimedTaskDataEntity>> {
        return taskDatabase.timedTaskDao().getAllTimedTasks()
    }

    override suspend fun addNewTimedTask(name: String, duration: Duration): TimedTaskDataEntity? {
        val insertEntity = TimedTaskDataEntity(
            name = name,
            initialDuration = duration,
            durationRemaining = duration,
            createdAt = Instant.now(),
        )
        val newEntityId = taskDatabase.timedTaskDao().insertTimedTask(insertEntity)
        // TODO come up with better error/null indicators
        if (newEntityId == -1L) {
            appLogger.error("Failed to insert timed task: $insertEntity")
            return null
        }
        return insertEntity.copy(taskId = newEntityId)
    }

    override suspend fun getTimedTaskById(taskId: Long): TimedTaskDataEntity? {
        return taskDatabase.timedTaskDao().getTimedTaskById(taskId)
    }

    override suspend fun updateTimedTask(updatedTimedTask: TimedTaskDataEntity): Boolean {
        return taskDatabase.timedTaskDao().updateTimedTask(updatedTimedTask) == 1
    }
}