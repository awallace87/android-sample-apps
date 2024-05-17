package work.wander.videoclip.data.recordings

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class VideoRecordingsDataModule {

    @Provides
    @Singleton
    fun provideVideoRecordingDatabase(
        @ApplicationContext context: Context
    ): VideoRecordingDatabase {
        return Room.databaseBuilder(
            context,
            VideoRecordingDatabase::class.java,
            "video_recordings_database"
        ).fallbackToDestructiveMigration().build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoRecordingsDataBindsModule {

    @Binds
    @Singleton
    abstract fun provideVideoRecordingsRepository(defaultVideoRecordingsRepository: DefaultVideoRecordingsRepository): VideoRecordingsRepository
}