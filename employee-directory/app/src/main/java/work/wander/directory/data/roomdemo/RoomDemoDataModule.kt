package work.wander.directory.data.roomdemo

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
class RoomDemoDataModule {

    @Provides
    @Singleton
    fun provideExampleRoomDatabase(
        @ApplicationContext applicationContext: Context,
    ): DemoRoomDatabase {
        return Room.databaseBuilder(
            context = applicationContext,
            DemoRoomDatabase::class.java,
            "example_database",
        ).fallbackToDestructiveMigration().build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomDemoDataBindsModule {

    @Binds
    @Singleton
    abstract fun bindsRoomDemoDataRepository(
        defaultRoomDemoDataRepository: DefaultRoomDemoDataRepository
    ): RoomDemoDataRepository

}