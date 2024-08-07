package work.wander.funnyface.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import work.wander.funnyface.proto.settings.ApplicationSettings
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationSettingsDataModule {

    @Singleton
    @Provides
    fun provideApplicationSettingsDataStore(
        @ApplicationContext applicationContext: Context
    ) : DataStore<ApplicationSettings> {
        return DataStoreFactory.create(
            serializer = ApplicationSettingsSerializer,
            produceFile = {
                applicationContext.filesDir.resolve("application_settings.pb")
            }
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationSettingsDataBindsModule {

    @Binds
    abstract fun bindApplicationSettingsRepository(
        defaultApplicationSettingsRepository: DefaultApplicationSettingsRepository
    ): ApplicationSettingsRepository

}