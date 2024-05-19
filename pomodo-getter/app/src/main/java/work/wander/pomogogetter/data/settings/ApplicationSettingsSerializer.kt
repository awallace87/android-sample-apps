package work.wander.pomogogetter.data.settings

import androidx.datastore.core.Serializer
import work.wander.pomogogetter.proto.settings.ApplicationSettings
import java.io.InputStream
import java.io.OutputStream

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