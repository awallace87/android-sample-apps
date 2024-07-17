package work.wander.funnyface.camera

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import com.google.common.util.concurrent.ListenableFuture
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForCameraX

@Module
@InstallIn(SingletonComponent::class)
class CameraXApplicationModule {

    @Provides
    @Singleton
    fun provideLifecycleCameraController(
        @ApplicationContext context: Context
    ): LifecycleCameraController {
        return LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_ANALYSIS)
        }
    }

    @Provides
    @Singleton
    fun provideCameraXProcessCameraProvider(
        @ApplicationContext context: Context
    ): ListenableFuture<ProcessCameraProvider> {
        return ProcessCameraProvider.getInstance(context)
    }

    @Provides
    @Singleton
    @ForCameraX
    fun provideCameraXCoroutineScope(
        @ForCameraX coroutineDispatcher: CoroutineDispatcher,
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + coroutineDispatcher)
    }

    @Provides
    @Singleton
    @ForCameraX
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class CameraXApplicationBindsModule {

    @Binds
    @Singleton
    abstract fun provideCameraXMonitor(
        cameraXMonitor: DefaultCameraXManager
    ): CameraXManager
}