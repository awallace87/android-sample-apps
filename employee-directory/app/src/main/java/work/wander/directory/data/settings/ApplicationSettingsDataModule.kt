package work.wander.directory.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import work.wander.directory.proto.settings.ApplicationSettings
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Annotation class for marking dependencies as being used for application settings data.
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ForApplicationSettingsData

/**
 * Dagger module for providing dependencies related to the application settings data store.
 */
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
            },
            migrations = listOf(
                InitialApplicationSettingsMigration()
            )
        )
    }

    @Provides
    @Singleton
    @ForApplicationSettingsData
    fun provideCoroutineDispatcher() : CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationSettingsDataBindsModule {

    @Binds
    abstract fun bindApplicationSettingsRepository(
        defaultApplicationSettingsRepository: DefaultApplicationSettingsRepository
    ): ApplicationSettingsRepository

}