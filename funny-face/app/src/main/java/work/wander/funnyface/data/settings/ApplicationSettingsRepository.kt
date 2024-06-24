package work.wander.funnyface.data.settings

import kotlinx.coroutines.flow.Flow
import work.wander.funnyface.proto.settings.ApplicationSettings

interface ApplicationSettingsRepository {

    fun getApplicationSettings(): Flow<ApplicationSettings>

    suspend fun updateApplicationSettings(applicationSettings: ApplicationSettings)
}