package work.wander.videoclip.framework.application

import androidx.annotation.MainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import work.wander.videoclip.framework.annotation.BackgroundThread
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class CoroutineModule {

    @Provides
    @Singleton
    @BackgroundThread
    fun provideBackgroundThreadDispatcher() : CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    @MainThread
    fun provideMainThreadDispatcher() : CoroutineDispatcher {
        return Dispatchers.Main
    }
}