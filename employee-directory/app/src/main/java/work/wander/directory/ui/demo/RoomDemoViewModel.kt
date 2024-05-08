package work.wander.directory.ui.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import work.wander.directory.data.roomdemo.RoomDemoDataRepository
import work.wander.directory.data.roomdemo.entity.DemoEntity
import work.wander.directory.framework.logging.ExampleLogger
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class RoomDemoViewModel @Inject constructor(
    private val roomDemoRepository: RoomDemoDataRepository,
    private val exampleLogger: ExampleLogger,
) : ViewModel() {

    private val _entities = roomDemoRepository.getAll()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis = Duration.ofSeconds(1).toMillis()),
            emptyList()
        )

    fun getAllEntities(): StateFlow<List<DemoEntity>> {
        return _entities
    }

    fun addNewEntity(data: String) {
        viewModelScope.launch {
            roomDemoRepository.insertData(data)
        }
    }

    fun updateEntity(id: Int, data: String) {
        viewModelScope.launch {
            roomDemoRepository.updateData(id, data)
        }
    }

    fun deleteEntity(entityId: Int) {
        viewModelScope.launch {
            val entity = _entities.value.find { it.id == entityId }
            if (entity != null) {
                roomDemoRepository.deleteData(entity)
            } else {
                exampleLogger.error("Entity with id $entityId not found for deletion.")
            }
        }
    }
}