package work.wander.pomodogetter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import work.wander.pomodogetter.data.tasks.TaskDataRepository
import work.wander.pomodogetter.framework.annotation.BackgroundThread
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskDataRepository: TaskDataRepository,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher
) : ViewModel() {

    val tasks = taskDataRepository.getAllTasks()

    fun addNewTask(name: String) {
        viewModelScope.launch(backgroundDispatcher) {
            val newTaskEntity = taskDataRepository.addNewTask(name)
        }
    }
}