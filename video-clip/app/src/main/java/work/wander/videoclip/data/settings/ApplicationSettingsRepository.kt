package work.wander.videoclip.data.settings

import kotlinx.coroutines.flow.Flow
import work.wander.videoclip.proto.settings.ApplicationSettings

interface ApplicationSettingsRepository {

    fun getApplicationSettings(): Flow<ApplicationSettings>

    suspend fun updateApplicationSettings(applicationSettings: ApplicationSettings)
}