package work.wander.directory.data.settings

import androidx.datastore.core.DataMigration
import work.wander.directory.data.employee.remote.EmployeeDataUrl
import work.wander.directory.proto.settings.ApplicationSettings

class InitialApplicationSettingsMigration : DataMigration<ApplicationSettings> {

    override suspend fun shouldMigrate(currentData: ApplicationSettings): Boolean {
        val networkSettings = currentData.activeEmployeeDataUrl
        return networkSettings.isEmpty()
    }

    override suspend fun migrate(currentData: ApplicationSettings): ApplicationSettings {
        return currentData.toBuilder()
            // Initialize to valid employee data endpoint
            .setActiveEmployeeDataUrl(EmployeeDataUrl.Complete.url)
            .build()
    }

    override suspend fun cleanUp() {
        // No cleanup necessary
    }
}