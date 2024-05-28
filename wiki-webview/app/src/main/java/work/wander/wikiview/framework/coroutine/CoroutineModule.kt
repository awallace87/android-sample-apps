package work.wander.wikiview.framework.coroutine

import androidx.annotation.MainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import work.wander.wikiview.framework.annotation.BackgroundThread
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoroutineModule {

    @Provides
    @Singleton
    @BackgroundThread
    fun provideBackgroundDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    @MainThread
    fun provideMainDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }
}