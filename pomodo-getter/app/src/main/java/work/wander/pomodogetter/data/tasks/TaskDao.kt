package work.wander.pomodogetter.data.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskDataEntity>>

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Long): TaskDataEntity?

    @Insert
    suspend fun insertTask(taskDataEntity: TaskDataEntity) : Long

    @Update
    suspend fun updateTask(taskDataEntity: TaskDataEntity) : Int
}