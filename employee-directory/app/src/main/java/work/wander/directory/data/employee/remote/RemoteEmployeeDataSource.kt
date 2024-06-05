package work.wander.directory.data.employee.remote

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import work.wander.directory.data.employee.room.EmployeeDatabase
import work.wander.directory.data.employee.room.EmployeeEntity
import work.wander.directory.data.settings.ApplicationSettingsRepository
import work.wander.directory.framework.logging.AppLogger
import javax.inject.Inject

/**
 * Repository for fetching/saving employee data.
 */
interface RemoteEmployeeDataSource {

    /**
     * Refreshes the employee data from the remote data source.
     */
    suspend fun refreshDataFromRemote(): EmployeeDataResponse

}

/**
 * Sealed class representing the possible responses from [refreshDataFromRemote].
 */
sealed interface EmployeeDataResponse {
    data class Success(val employees: List<RemoteEmployeeData>) : EmployeeDataResponse
    data class Error(val message: String) : EmployeeDataResponse
}

/**
 * Default implementation of [RemoteEmployeeDataSource] that uses a remote data source.
 */
class DefaultRemoteEmployeeDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val employeeDatabase: EmployeeDatabase,
    private val appLogger: AppLogger,
    applicationSettingsRepository: ApplicationSettingsRepository,
    @ForEmployeeRequest private val coroutineScope: CoroutineScope,
    @ForEmployeeRequest private val dispatcher: CoroutineDispatcher,
) : RemoteEmployeeDataSource {

    private val dataSourceUrl =
        applicationSettingsRepository.getApplicationSettings().map { settings ->
            EmployeeDataUrl.fromUrl(settings.activeEmployeeDataUrl)
        }.stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            EmployeeDataUrl.Complete
        )


    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(EmployeeTypeJsonAdapter())
        .build()

    override suspend fun refreshDataFromRemote(): EmployeeDataResponse {
        return withContext(dispatcher) {
            val currentActiveUrl = dataSourceUrl.value
            if (currentActiveUrl == EmployeeDataUrl.Unknown) {
                appLogger.warn("No active data source URL found. Cancelling fetch request.")
                return@withContext EmployeeDataResponse.Error("No active data source URL present")
            }

            val request = okhttp3.Request.Builder()
                .url(dataSourceUrl.value.url)
                .build()
            appLogger.info("Fetching employee data from ${dataSourceUrl.value.url}")
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val json = response.body?.string()
                if (json != null) {
                    val parsedData = parseJsonToEmployeeData(json)
                    employeeDatabase.employeeDao().insertEmployees(parsedData.map {
                        it.toEmployeeEntity()
                    })
                    return@withContext EmployeeDataResponse.Success(parsedData)
                }
                appLogger.error("Failed to parse server response")
                return@withContext EmployeeDataResponse.Error("Failed to parse response")
            } else {
                appLogger.error("Failed to fetch data from server ${response.code} ${response.message} ${response.body?.string() ?: ""}")
                return@withContext EmployeeDataResponse.Error("Failed to fetch data")
            }
        }
    }

    /**
     * Parses the given JSON response into a list of [RemoteEmployeeData].
     */
    private fun parseJsonToEmployeeData(json: String): List<RemoteEmployeeData> {
        val adapter: JsonAdapter<RemoteEmployeeDataResponseBody> =
            moshi.adapter(RemoteEmployeeDataResponseBody::class.java)
        val employeeDataWrapper = adapter.fromJson(json)
        return employeeDataWrapper?.employees ?: emptyList()
    }

}

/**
 * Extension function to convert a [RemoteEmployeeData] to an [EmployeeEntity].
 */
fun RemoteEmployeeData.toEmployeeEntity(): EmployeeEntity {
    return EmployeeEntity(
        id = id,
        fullName = fullName,
        phoneNumber = phoneNumber,
        emailAddress = emailAddress,
        biography = biography,
        photoUrlSmall = photoUrlSmall,
        photoUrlLarge = photoUrlLarge,
        team = team,
        employeeType = employeeType
    )
}