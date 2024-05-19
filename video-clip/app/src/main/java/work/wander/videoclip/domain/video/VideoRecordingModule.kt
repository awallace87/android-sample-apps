package work.wander.videoclip.domain.video

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForVideoRecording

@Module
@InstallIn(SingletonComponent::class)
class VideoRecordingModule {

    @Provides
    @Singleton
    @ForVideoRecording
    fun provideVideoRecordingDirectory(
        @ApplicationContext context: Context
    ) : File {
        return context.filesDir.resolve("video-clips").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class VideoRecordingBindsModule {

    @Binds
    @Singleton
    abstract fun bindVideoRecorder(videoRecorder: CameraXVideoRecorder): VideoRecorder

    @Binds
    @Singleton
    abstract fun bindPreviewImageUpdater(previewImageUpdater: DefaultPreviewImageUpdater): PreviewImageUpdater
}
