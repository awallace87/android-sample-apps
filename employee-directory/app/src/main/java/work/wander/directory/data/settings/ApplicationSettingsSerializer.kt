package work.wander.directory.data.settings

import androidx.datastore.core.Serializer
import work.wander.directory.proto.settings.ApplicationSettings
import java.io.InputStream
import java.io.OutputStream

/**
 * Serializer for the application settings. Used by the DataStore to serialize and deserialize the application settings.
 */
object ApplicationSettingsSerializer: Serializer<ApplicationSettings> {
    override val defaultValue: ApplicationSettings
        get() = ApplicationSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ApplicationSettings {
        return ApplicationSettings.parseFrom(input)
    }

    override suspend fun writeTo(t: ApplicationSettings, output: OutputStream) {
        t.writeTo(output)
    }
}