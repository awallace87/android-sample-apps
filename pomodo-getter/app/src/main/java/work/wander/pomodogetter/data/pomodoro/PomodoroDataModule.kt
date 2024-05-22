package work.wander.pomodogetter.data.pomodoro

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PomodoroDataModule {

    @Provides
    @Singleton
    fun providePomodoroDatabase(
        @ApplicationContext context: Context
    ): PomodoroDatabase {
        return Room.databaseBuilder(
            context,
            PomodoroDatabase::class.java,
            "pomodoro_database"
        ).fallbackToDestructiveMigration()
            .build()
    }
}