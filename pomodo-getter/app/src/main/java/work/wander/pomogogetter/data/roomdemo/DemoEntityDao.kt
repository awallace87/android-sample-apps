package work.wander.pomogogetter.data.roomdemo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import work.wander.pomogogetter.data.roomdemo.entity.DemoEntity

@Dao
interface DemoEntityDao {

    @Query("SELECT * FROM examples")
    fun getAll(): Flow<List<DemoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(demoEntity: DemoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateEntity(demoEntity: DemoEntity)

    @Delete
    suspend fun deleteEntity(demoEntity: DemoEntity)
}