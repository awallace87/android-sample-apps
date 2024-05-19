package work.wander.pomodogetter.data.settings

import kotlinx.coroutines.flow.Flow
import work.wander.pomogogetter.proto.settings.ApplicationSettings

interface ApplicationSettingsRepository {

    fun getApplicationSettings(): Flow<ApplicationSettings>

    suspend fun updateApplicationSettings(applicationSettings: ApplicationSettings)
}