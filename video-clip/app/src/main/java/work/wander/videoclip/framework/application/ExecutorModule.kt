package work.wander.videoclip.framework.application

import android.content.Context
import androidx.annotation.MainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import work.wander.videoclip.framework.annotation.BackgroundThread
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Module
@InstallIn(SingletonComponent::class)
class ExecutorModule {

    @Provides
    @MainThread
    fun provideMainThreadExecutor(@ApplicationContext context: Context) : Executor{
        return context.mainExecutor
    }

    @Provides
    @BackgroundThread
    fun provideBackgroundThreadExecutor() : ExecutorService {
        return Executors.newCachedThreadPool()
    }

    @Provides
    @BackgroundThread
    fun provideBackgroundExecutor(@BackgroundThread executor: ExecutorService): Executor {
        return executor
    }
}