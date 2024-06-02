package work.wander.pomodogetter.ui.pomodoro

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import work.wander.pomodogetter.data.tasks.TaskDataRepository
import work.wander.pomodogetter.data.tasks.entity.TimedTaskDataEntity
import work.wander.pomodogetter.framework.logging.AppLogger
import work.wander.pomodogetter.framework.time.TimerManager
import work.wander.pomodogetter.framework.toast.Toaster
import work.wander.pomodogetter.service.pomodoro.PomodoroTimerService
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class PomodoroTimerViewModelTest {

    private lateinit var viewModel: PomodoroTimerViewModel
    private val timerManager = mockk<TimerManager>()
    private val pomodoroServiceLauncher = mockk<PomodoroTimerService.Launcher>(relaxed = true)
    private val taskDataRepository = mockk<TaskDataRepository>()
    private val toaster = mockk<Toaster>(relaxed = true)
    private val logger = mockk<AppLogger>(relaxed = true)

    private val timerStateFlow =
        MutableStateFlow<TimerManager.TimerState>(TimerManager.TimerState.Uninitialized)

    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)

    lateinit var uiState: StateFlow<PomodoroTimerUiState>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { taskDataRepository.getTimedTaskById(any()) } returns null
        coEvery { timerManager.getTimerState(any()) } returns timerStateFlow

        viewModel = PomodoroTimerViewModel(
            timerManager,
            pomodoroServiceLauncher,
            taskDataRepository,
            testDispatcher,
            toaster,
            logger
        )

        uiState = viewModel.uiState
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onConstruction with uninitialized timer UI state is uninitialized`() = runTest {

        assertThat(PomodoroTimerUiState.Initial).isEqualTo(uiState.value)
    }

    @Test
    fun `onConstruction with ready timer UI state is uninitialized`() = runTest {
        timerStateFlow.update { TimerManager.TimerState.Ready(30.minutes, 1000) }

        testCoroutineScheduler.advanceUntilIdle()

        assertThat(PomodoroTimerUiState.Ready(null, 30.minutes)).isEqualTo(uiState.value)
    }

    @Test
    fun `onTimerReady calls resetTimer when no task is bound`() = runTest {
        viewModel.onTimerReady()

        coVerify { pomodoroServiceLauncher.resetTimer(any()) }
    }

    @Test
    fun `onTimerReady does not call resetTimer when a task is bound`() = runTest {
        coEvery { taskDataRepository.getTimedTaskById(1) } returns TimedTaskDataEntity(
            1,
            "Task 1",
            null,
            15.minutes,
            createdAt = Instant.now()
        )

        viewModel.setTimedTaskId(1)
        testCoroutineScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { pomodoroServiceLauncher.resetTimer(15.minutes) }

        viewModel.onTimerReady()

        coVerify(exactly = 1) { pomodoroServiceLauncher.resetTimer(15.minutes) }
    }

    @Test
    fun `setInitialDuration calls resetTimer when no task is bound`() = runTest {
        viewModel.setInitialDuration(20.minutes)

        coVerify { pomodoroServiceLauncher.resetTimer(20.minutes) }
    }

    @Test
    fun `setInitialDuration does not call resetTimer when a task is bound`() = runTest {
        coEvery { taskDataRepository.getTimedTaskById(1) } returns TimedTaskDataEntity(
            1,
            "Task 1",
            null,
            15.minutes,
            createdAt = Instant.now()
        )

        viewModel.setTimedTaskId(1)
        testCoroutineScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { pomodoroServiceLauncher.resetTimer(15.minutes) }

        viewModel.setInitialDuration(20.minutes)
        testCoroutineScheduler.advanceUntilIdle()

        coVerify(exactly = 0) { pomodoroServiceLauncher.resetTimer(20.minutes) }
    }

    @Test
    fun `setTimedTaskId does not set boundTask when task is completed`() = runTest {
        coEvery { taskDataRepository.getTimedTaskById(1) } returns TimedTaskDataEntity(
            1,
            "Task 1",
            null,
            15.minutes,
            createdAt = Instant.now().minusMillis(TimeUnit.DAYS.toMillis(1)),
            completedAt = Instant.now()
        )

        viewModel.setTimedTaskId(1)
        testCoroutineScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { toaster.showToast(any()) }
        assertThat(viewModel.boundTask).isNull()
    }

    @Test
    fun `setTimedTaskId sets boundTask when task is found`() = runTest {
        coEvery { taskDataRepository.getTimedTaskById(1) } returns TimedTaskDataEntity(
            1,
            "Task 1",
            null,
            15.minutes,
            createdAt = Instant.now()
        )

        viewModel.setTimedTaskId(1)
        testCoroutineScheduler.advanceUntilIdle()

        assertThat(viewModel.boundTask).isNotNull()
    }

    @Test
    fun `setTimedTaskId does not set boundTask when task is not found`() = runTest {
        coEvery { taskDataRepository.getTimedTaskById(1) } returns null

        viewModel.setTimedTaskId(1)

        assertThat(viewModel.boundTask).isNull()
    }

    @Test
    fun `startTimer does not call startTimer when UI state is not ready`() = runTest {
        viewModel.startTimer()

        testCoroutineScheduler.advanceUntilIdle()

        coVerify(exactly = 0) { pomodoroServiceLauncher.startTimer() }
    }

    @Test
    fun `startTimer calls startTimer when UI state is ready`() = runTest {
        timerStateFlow.update { TimerManager.TimerState.Ready(30.minutes, 1000) }

        testCoroutineScheduler.advanceUntilIdle()

        viewModel.startTimer()

        testCoroutineScheduler.advanceUntilIdle()

        coVerify { pomodoroServiceLauncher.startTimer() }
    }

    @Test
    fun `startTimer updates bound task when started`() = runTest {
        val createdAt = Instant.now()
        coEvery { taskDataRepository.getTimedTaskById(1) } returns TimedTaskDataEntity(
            1,
            "Task 1",
            null,
            15.minutes,
            createdAt = createdAt
        )
        coEvery { taskDataRepository.updateTimedTask(any()) } returns true

        viewModel.setTimedTaskId(1)
        testCoroutineScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { pomodoroServiceLauncher.resetTimer(15.minutes) }

        timerStateFlow.update { TimerManager.TimerState.Ready(15.minutes, 1000) }
        testCoroutineScheduler.advanceUntilIdle()

        assertThat(uiState.value).isEqualTo(
            PomodoroTimerUiState.Ready(
                PomodoroBoundTask(
                    1,
                    "Task 1",
                    15.minutes
                ), 15.minutes
            )
        )

        viewModel.startTimer()

        coVerify(exactly = 1) { pomodoroServiceLauncher.startTimer() }

        timerStateFlow.update {
            TimerManager.TimerState.Running(
                startedAt = Instant.now(),
                totalDuration = 15.minutes,
                remainingDuration = 15.minutes - 1.seconds,
                millisPerTick = 1000,
            )
        }
        testCoroutineScheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            taskDataRepository.updateTimedTask(
                TimedTaskDataEntity(
                    1,
                    "Task 1",
                    initialDuration = 15.minutes,
                    durationRemaining = 15.minutes - 1.seconds,
                    createdAt = createdAt,
                )
            )
        }
    }

    @Test
    fun `startTimer does not call timer service when timer is already running`() = runTest {
        timerStateFlow.update {
            TimerManager.TimerState.Running(
                startedAt = Instant.now(),
                totalDuration = 15.minutes,
                remainingDuration = 15.minutes - 1.seconds,
                millisPerTick = 1000,
            )
        }
        testCoroutineScheduler.advanceUntilIdle()

        assertThat(uiState.value).isEqualTo(
            PomodoroTimerUiState.Running(
                remainingDuration = 15.minutes - 1.seconds,
                totalDuration = 15.minutes,
            )
        )

        viewModel.startTimer()

        coVerify(exactly = 0) { pomodoroServiceLauncher.startTimer() }
    }

    @Test
    fun `pauseTimer does not call pauseTimer when UI state is not running`() = runTest {
        timerStateFlow.update { TimerManager.TimerState.Ready(30.minutes, 1000) }
        testCoroutineScheduler.advanceUntilIdle()

        assertThat(uiState.value).isEqualTo(
            PomodoroTimerUiState.Ready(
                initialDuration = 30.minutes
            )
        )
        coVerify(exactly = 0) { pomodoroServiceLauncher.pauseTimer() }

        viewModel.pauseTimer()

        coVerify(exactly = 0) { pomodoroServiceLauncher.pauseTimer() }

        val startedAt = Instant.now().minusMillis(30.minutes.inWholeMilliseconds)
        timerStateFlow.update {
            TimerManager.TimerState.Paused(
                startedAt = startedAt,
                remainingDuration = 29.minutes,
                totalDuration = 30.minutes,
                millisPerTick = 1000,
            )
        }
        testCoroutineScheduler.advanceUntilIdle()

        assertThat(uiState.value).isEqualTo(
            PomodoroTimerUiState.Paused(
                remainingDuration = 29.minutes,
                totalDuration = 30.minutes
            )
        )
        coVerify(exactly = 0) { pomodoroServiceLauncher.pauseTimer() }

        viewModel.pauseTimer()
        testCoroutineScheduler.advanceUntilIdle()

        coVerify(exactly = 0) { pomodoroServiceLauncher.pauseTimer() }
    }


}