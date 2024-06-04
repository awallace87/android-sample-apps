package work.wander.directory.data.employee.remote

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import work.wander.directory.data.employee.room.EmployeeDatabase
import work.wander.directory.data.settings.ApplicationSettingsRepository
import work.wander.directory.framework.logging.AppLogger

class DefaultRemoteEmployeeDataSourceTest {

    private val okHttpClient = mockk<OkHttpClient>()
    private val employeeDatabase = mockk<EmployeeDatabase>(relaxed = true)
    private val appLogger = mockk<AppLogger>(relaxed = true)
    private val applicationSettingsRepository = mockk<ApplicationSettingsRepository>(relaxed = true)
    private val coroutineScope = TestScope()
    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler)

    private lateinit var dataSource: DefaultRemoteEmployeeDataSource

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        dataSource = DefaultRemoteEmployeeDataSource(
            okHttpClient,
            employeeDatabase,
            appLogger,
            applicationSettingsRepository,
            coroutineScope,
            dispatcher
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshDataFromRemote returns Success when response is successful and valid`() = runTest {
        val responseBody = valid_employee_data_response.toResponseBody("application/json".toMediaType())
        val response = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost").build())
            .body(responseBody)
            .build()

        coEvery { okHttpClient.newCall(any()).execute() } returns response

        val result = dataSource.refreshDataFromRemote()
        scheduler.advanceUntilIdle()

        assertThat(result).isInstanceOf(EmployeeDataResponse.Success::class.java)
    }

    @Test
    fun `refreshDataFromRemote returns Error when response is unsuccessful`() = runTest {
        val responseBody = empty_employee_data_response.toResponseBody("application/json".toMediaType())
        val response = Response.Builder()
            .code(400)
            .message("Bad Request")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost").build())
            .body(responseBody)
            .build()

        coEvery { okHttpClient.newCall(any()).execute() } returns response

        val result = dataSource.refreshDataFromRemote()

        assertThat(result).isInstanceOf(EmployeeDataResponse.Error::class.java)
        assertThat((result as EmployeeDataResponse.Error).message).isEqualTo("Failed to fetch data")
    }

    @Test
    fun `refreshDataFromRemote returns success when response is successful but partially malformed`() = runTest {
        val responseBody = malformed_employee_data_response.toResponseBody("application/json".toMediaType())
        val response = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("http://localhost").build())
            .body(responseBody)
            .build()

        coEvery { okHttpClient.newCall(any()).execute() } returns response

        val result = dataSource.refreshDataFromRemote()

        assertThat(result).isInstanceOf(EmployeeDataResponse.Success::class.java)
    }

}