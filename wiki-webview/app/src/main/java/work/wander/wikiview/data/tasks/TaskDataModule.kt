package work.wander.wikiview.data.tasks

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
class TaskDataModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext context: Context
    ): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "task_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskDataBindsModule {

    @Singleton
    @Binds
    abstract fun bindTaskDataRepository(
        defaultTaskDataRepository: DefaultTaskDataRepository
    ): TaskDataRepository

}