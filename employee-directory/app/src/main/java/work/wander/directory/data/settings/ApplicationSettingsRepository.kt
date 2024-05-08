package work.wander.directory.data.settings

import kotlinx.coroutines.flow.Flow
import work.wander.directory.proto.settings.ApplicationSettings

interface ApplicationSettingsRepository {

    fun getApplicationSettings(): Flow<ApplicationSettings>

    suspend fun updateApplicationSettings(applicationSettings: ApplicationSettings)
}