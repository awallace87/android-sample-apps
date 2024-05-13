package work.wander.directory.data.settings

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import work.wander.directory.proto.settings.ApplicationSettings
import javax.inject.Inject

/**
 * Default implementation of [ApplicationSettingsRepository].
 */
class DefaultApplicationSettingsRepository @Inject constructor(
    private val applicationSettingsDataStore: DataStore<ApplicationSettings>
) : ApplicationSettingsRepository {

    override fun getApplicationSettings(): Flow<ApplicationSettings> {
        return applicationSettingsDataStore.data
    }

    override suspend fun updateApplicationSettings(applicationSettings: ApplicationSettings) {
        applicationSettingsDataStore.updateData {
            applicationSettings
        }
    }
}