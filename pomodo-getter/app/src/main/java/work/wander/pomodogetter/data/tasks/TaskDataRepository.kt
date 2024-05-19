package work.wander.pomodogetter.data.tasks

import kotlinx.coroutines.flow.Flow
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity
import java.time.Instant
import javax.inject.Inject

interface TaskDataRepository {

    fun getAllTasks(): Flow<List<TaskDataEntity>>

    suspend fun addNewTask(name: String): TaskDataEntity

}

class DefaultTaskDataRepository @Inject constructor(
    private val taskDatabase: TaskDatabase,
) : TaskDataRepository {

    override fun getAllTasks(): Flow<List<TaskDataEntity>> {
        return taskDatabase.taskDao().getAllTasks()
    }

    override suspend fun addNewTask(name: String): TaskDataEntity {
        val newTaskEntity =
            TaskDataEntity(name = name, isCompleted = false, createdAt = Instant.now())
        taskDatabase.taskDao().insertTask(newTaskEntity)
        return newTaskEntity
    }
}