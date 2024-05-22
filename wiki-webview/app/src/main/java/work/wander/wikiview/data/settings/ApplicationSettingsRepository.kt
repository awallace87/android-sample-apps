package work.wander.wikiview.data.settings

import kotlinx.coroutines.flow.Flow
import work.wander.wikiview.proto.settings.ApplicationSettings

interface ApplicationSettingsRepository {

    fun getApplicationSettings(): Flow<ApplicationSettings>

    suspend fun updateApplicationSettings(applicationSettings: ApplicationSettings)
}