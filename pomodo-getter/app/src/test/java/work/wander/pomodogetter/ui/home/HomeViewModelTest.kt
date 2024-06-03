package work.wander.pomodogetter.ui.home

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import work.wander.pomodogetter.data.tasks.TaskDataRepository
import work.wander.pomodogetter.data.tasks.entity.TaskDataEntity
import work.wander.pomodogetter.data.tasks.entity.TimedTaskDataEntity
import work.wander.pomodogetter.framework.logging.AppLogger
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel

    private val taskDataRepository = mockk<TaskDataRepository>()

    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)

    private val logger = mockk<AppLogger>(relaxed = true)

    private val tasksPresentFlow = MutableStateFlow<List<TaskDataEntity>>(emptyList())

    private val timedTasksPresentFlow = MutableStateFlow<List<TimedTaskDataEntity>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { taskDataRepository.getAllTasks() } returns tasksPresentFlow
        coEvery { taskDataRepository.getAllTimedTasks() } returns timedTasksPresentFlow
        coEvery { taskDataRepository.updateTask(any()) } returns false

        viewModel = HomeViewModel(taskDataRepository, testDispatcher, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addNewTask adds a new task`() = runTest {
        val taskName = "New Task"
        val createdAt = Instant.now()
        coEvery { taskDataRepository.addNewTask(taskName) } returns TaskDataEntity(
            1,
            taskName,
            false,
            createdAt
        )

        viewModel.addNewTask(taskName)
        testCoroutineScheduler.advanceUntilIdle()

        coVerify { taskDataRepository.addNewTask(taskName) }
    }

    @Test
    fun `toggleTaskCompletion toggles task completion`() = runTest {
        val taskId = 1L
        val isCompleted = false
        val createdAt = Instant.now()
        coEvery { taskDataRepository.getTaskById(1L) } returns TaskDataEntity(
            taskId,
            "Task",
            !isCompleted,
            createdAt
        )

        viewModel.toggleTaskCompletion(taskId, isCompleted)
        testCoroutineScheduler.advanceUntilIdle()

        coVerify { taskDataRepository.updateTask(
            TaskDataEntity(
                taskId,
                "Task",
                isCompleted,
                createdAt
            )
        ) }
    }

    @Test
    fun `addNewTimedTask adds a new timed task`() = runTest {
        val taskName = "New Timed Task"
        val duration = 30.minutes
        val createdAt = Instant.now()
        coEvery { taskDataRepository.addNewTimedTask(taskName, duration) } returns TimedTaskDataEntity(
            taskId = 1,
            name = taskName,
            initialDuration = duration,
            durationRemaining = duration,
            createdAt = createdAt,
        )

        viewModel.addNewTimedTask(taskName, duration)
        testCoroutineScheduler.advanceUntilIdle()

        coVerify { taskDataRepository.addNewTimedTask(taskName, duration) }
    }
}