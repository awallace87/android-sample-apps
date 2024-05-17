package work.wander.videoclip.framework.camerax.legacy

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class Camera2


@Module
@InstallIn(SingletonComponent::class)
class CameraLegacyApplicationModule {

    @Provides
    @Singleton
    fun provideCamera2Manager(
        @ApplicationContext context: Context
    ): CameraManager {
        return context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    @Provides
    @Singleton
    @Camera2
    fun provideCameraCharacteristics(
        cameraManager: CameraManager
    ): List<CameraCharacteristics> {
        return cameraManager.cameraIdList.map {
            cameraManager.getCameraCharacteristics(it)
        }
    }

    @Provides
    @Singleton
    @Camera2
    fun provideCameraDeviceId(
        cameraManager: CameraManager
    ): CameraDeviceId {
        return CameraDeviceId(
            camera2DeviceIds = cameraManager.cameraIdList.toList(),
            camera2DeviceCharacteristics = cameraManager.cameraIdList.map {
                cameraManager.getCameraCharacteristics(it)
            }
        )
    }

}