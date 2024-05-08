package work.wander.directory.data.roomdemo

import kotlinx.coroutines.flow.Flow
import work.wander.directory.data.roomdemo.entity.DemoEntity
import java.time.Instant
import javax.inject.Inject

class DefaultRoomDemoDataRepository @Inject constructor(
    private val demoRoomDatabase: DemoRoomDatabase
) : RoomDemoDataRepository {
    override fun getAll(): Flow<List<DemoEntity>> {
        return demoRoomDatabase.demoEntityDao().getAll()
    }

    override suspend fun insertData(data: String) {
        return demoRoomDatabase.demoEntityDao().insertEntity(
            DemoEntity(data = data)
        )
    }

    override suspend fun updateData(id: Int, data: String) {
        return demoRoomDatabase.demoEntityDao().updateEntity(
            DemoEntity(id = id, data = data, lastModifiedDate = Instant.now())
        )
    }

    override suspend fun deleteData(entity: DemoEntity) {
        demoRoomDatabase.demoEntityDao().deleteEntity(entity)
    }
}