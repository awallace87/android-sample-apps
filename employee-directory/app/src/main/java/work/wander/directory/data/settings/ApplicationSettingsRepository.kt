package work.wander.directory.data.settings

import kotlinx.coroutines.flow.Flow
import work.wander.directory.proto.settings.ApplicationSettings

/**
 * Repository for fetching/saving application settings.
 */
interface ApplicationSettingsRepository {

    /**
     * Returns a [Flow] of the application settings.
     */
    fun getApplicationSettings(): Flow<ApplicationSettings>

    /**
     * Updates the application settings.
     */
    suspend fun updateApplicationSettings(applicationSettings: ApplicationSettings)
}