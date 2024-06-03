package work.wander.pomodogetter.ui.task

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import work.wander.pomodogetter.data.tasks.TaskDataRepository
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity
import work.wander.pomodogetter.framework.logging.AppLogger
import java.time.Instant

@ExperimentalCoroutinesApi
class TaskDetailViewModelTest {

    private val taskDataRepository = mockk<TaskDataRepository>()
    private val appLogger = mockk<AppLogger>()

    private lateinit var viewModel: TaskDetailViewModel

    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TaskDetailViewModel(taskDataRepository, testDispatcher, appLogger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setTaskId updates uiState to Loading`() = runBlockingTest {
        val taskId = 1L
        viewModel.setTaskId(taskId)
        assertThat(viewModel.uiState.value).isEqualTo(TaskDetailUiState.Loading(taskId))
    }

    @Test
    fun `setTaskId updates uiState to TaskDataLoaded when task exists`() = runBlockingTest {
        val taskId = 1L
        val createdAt = Instant.now()
        val taskDataEntity = TaskDataEntity(taskId, "Test Task", false, createdAt)
        coEvery { taskDataRepository.getTaskById(taskId) } returns taskDataEntity

        viewModel.setTaskId(taskId)
        testCoroutineScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value).isEqualTo(TaskDetailUiState.TaskDataLoaded(taskDataEntity))
    }

    @Test
    fun `setTaskId updates uiState to TaskNotFound when task does not exist`() = runBlockingTest {
        val taskId = 1L
        coEvery { taskDataRepository.getTaskById(taskId) } returns null

        viewModel.setTaskId(taskId)
        testCoroutineScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value).isEqualTo(TaskDetailUiState.TaskNotFound)
    }

}