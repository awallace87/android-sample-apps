package work.wander.pomodogetter.ui.pomodoro

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import work.wander.pomodogetter.data.tasks.TaskDataRepository
import work.wander.pomodogetter.data.tasks.entity.TimedTaskDataEntity
import work.wander.pomodogetter.framework.logging.AppLogger
import work.wander.pomodogetter.framework.time.TimerManager
import work.wander.pomodogetter.service.pomodoro.PomodoroTimerService
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

@ExperimentalCoroutinesApi
class PomodoroTimerViewModelTest {

    private lateinit var viewModel: PomodoroTimerViewModel
    private val timerManager = mockk<TimerManager>()
    private val pomodoroServiceLauncher = mockk<PomodoroTimerService.Launcher>(relaxed = true)
    private val taskDataRepository = mockk<TaskDataRepository>()
    private val logger = mockk<AppLogger>(relaxed = true)

    private val timerStateFlow = MutableStateFlow<TimerManager.TimerState>(TimerManager.TimerState.Uninitialized)

    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { taskDataRepository.getTimedTaskById(any()) } returns null
        coEvery { timerManager.getTimerState(any()) } returns timerStateFlow

        viewModel = PomodoroTimerViewModel(
            timerManager,
            pomodoroServiceLauncher,
            taskDataRepository,
            Dispatchers.Unconfined,
            logger
        )
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onConstruction with uninitialized timer UI state is uninitialized`() = runTest {
        val uiState = viewModel.uiState

        assertEquals(PomodoroTimerUiState.Initial, uiState.value)
    }

    @Test
    fun `onConstruction with ready timer UI state is uninitialized`() = runTest {
        timerStateFlow.update {  TimerManager.TimerState.Ready(30.minutes, 1000) }

        testCoroutineScheduler.advanceUntilIdle()

        val uiState = viewModel.uiState

        assertEquals(PomodoroTimerUiState.Ready(null, 30.minutes), uiState.value)
    }

    @Test
    fun `onTimerReady calls resetTimer when no task is bound`() = runTest {
        viewModel.onTimerReady()

        coVerify { pomodoroServiceLauncher.resetTimer(any()) }
    }

    @Test
    fun `setInitialDuration calls resetTimer when no task is bound`() = runTest {
        viewModel.setInitialDuration(30.minutes)

        coVerify { pomodoroServiceLauncher.resetTimer(30.minutes) }
    }

    @Test
    fun `setTimedTaskId sets boundTask when task is found`() = runTest {
        val task = TimedTaskDataEntity(1, "Task 1", null, 30.minutes, createdAt = Instant.now())
        coEvery { taskDataRepository.getTimedTaskById(1) } returns task

        viewModel.setTimedTaskId(1)

        assertEquals(PomodoroBoundTask(1, "Task 1", 30.minutes), viewModel.boundTask)
    }

    @Test
    fun `setTimedTaskId does not set boundTask when task is not found`() = runTest {
        coEvery { taskDataRepository.getTimedTaskById(1) } returns null

        viewModel.setTimedTaskId(1)

        assertEquals(null, viewModel.boundTask)
    }
}