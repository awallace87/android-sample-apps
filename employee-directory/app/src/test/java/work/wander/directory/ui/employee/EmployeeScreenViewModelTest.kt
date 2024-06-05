package work.wander.directory.ui.employee

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import work.wander.directory.data.employee.room.EmployeeDao
import work.wander.directory.data.employee.room.EmployeeDatabase
import work.wander.directory.data.employee.room.EmployeeEntity
import work.wander.directory.data.employee.room.EmployeeType
import work.wander.directory.framework.logging.AppLogger
import work.wander.directory.framework.toast.Toaster

@ExperimentalCoroutinesApi
class EmployeeScreenViewModelTest {

    private val employeesStateFlow = MutableStateFlow<List<EmployeeEntity>>(emptyList())
    private val employeeDao = mockk<EmployeeDao>() {
        every { getAllEmployees() } returns employeesStateFlow
    }
    private val employeeDatabase = mockk<EmployeeDatabase> {
        every { employeeDao() } returns employeeDao
    }
    private val appLogger = mockk<AppLogger>(relaxed = true)
    private val toaster = mockk<Toaster>()

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler)


    private lateinit var viewModel: EmployeeScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        viewModel = EmployeeScreenViewModel(
            employeeDatabase,
            toaster,
            appLogger,
        )
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setEmployeeId updates uiState to Loading when employee id is set`() = runTest {
        viewModel.setEmployeeId("1")
        scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(EmployeeScreenUiState.Loading::class.java)
    }

    @Test
    fun `setEmployeeId updates uiState to Success when employee is found`() = runTest {
        val employees = listOf(
            EmployeeEntity(
                "1",
                "John Doe",
                "john.doe@example.com",
                biography = "bio",
                photoUrlSmall = "example.com/small.jpeg",
                photoUrlLarge = "example.com/large.jpeg",
                employeeType = EmployeeType.FULL_TIME,
            )
        )
        employeesStateFlow.value = employees
        scheduler.advanceUntilIdle()

        viewModel.setEmployeeId("1")
        scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(EmployeeScreenUiState.Success::class.java)
    }

    @Test
    fun `setEmployeeId updates uiState to Error when employee is not found`() = runTest {
        val employees = listOf(
            EmployeeEntity(
                "1",
                "John Doe",
                "john.doe@example.com",
                biography = "bio",
                photoUrlSmall = "example.com/small.jpeg",
                photoUrlLarge = "example.com/large.jpeg",
                employeeType = EmployeeType.FULL_TIME,
            )
        )
        employeesStateFlow.update { employees }
        scheduler.advanceUntilIdle()

        viewModel.setEmployeeId("1")

        assertThat(viewModel.uiState.value).isInstanceOf(EmployeeScreenUiState.Error::class.java)
    }
}