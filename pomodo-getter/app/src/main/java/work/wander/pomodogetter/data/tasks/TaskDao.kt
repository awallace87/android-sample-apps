package work.wander.pomodogetter.data.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskDataEntity>>

    @Insert
    suspend fun insertTask(taskDataEntity: TaskDataEntity)
}