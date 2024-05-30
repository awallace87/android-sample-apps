package work.wander.pomodogetter.data.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import work.wander.pomodogetter.data.tasks.entity.TimedTaskDataEntity

@Dao
interface TimedTaskDao {

    @Query("SELECT * FROM timed_tasks")
    fun getAllTimedTasks(): Flow<List<TimedTaskDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimedTask(timedTaskDataEntity: TimedTaskDataEntity) : Long

    @Query("SELECT * FROM timed_tasks WHERE taskId = :taskId")
    suspend fun getTimedTaskById(taskId: Long): TimedTaskDataEntity?

    @Update
    suspend fun updateTimedTask(timedTaskDataEntity: TimedTaskDataEntity) : Int
}