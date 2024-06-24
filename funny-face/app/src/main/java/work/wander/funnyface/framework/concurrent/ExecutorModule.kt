package work.wander.funnyface.framework.concurrent

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import work.wander.funnyface.framework.annotation.BackgroundThread
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ExecutorModule {

    @Provides
    @Singleton
    @MainThread
    fun provideMainThreadExecutor(@ApplicationContext context: Context): Executor {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.mainExecutor
        } else {
            // TODO: Remove this when we drop support for pre-Pie devices
            Handler(Looper.getMainLooper()).asCoroutineDispatcher("Pre-Pie Main Threads").asExecutor()
        }
    }

    @Provides
    @Singleton
    @BackgroundThread
    fun provideBackgroundThreadExecutor(): ExecutorService {
        return Executors.newCachedThreadPool()
    }

    @Provides
    @Singleton
    @BackgroundThread
    fun provideBackgroundExecutor(@BackgroundThread executor: ExecutorService): Executor {
        return executor
    }
}