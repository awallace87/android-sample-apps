package work.wander.videoclip.domain.image

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
annotation class ForThumbnailImages

@Module
@InstallIn(SingletonComponent::class)
class ImageSaverModule {

    @Provides
    @ForThumbnailImages
    @Singleton
    fun provideThumbnailImageDirectory(
        @ApplicationContext context: Context
    ): File {
        return File(context.filesDir, "thumbnails").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageSaverBindsModule {

    @Binds
    @Singleton
    abstract fun providesImageSaver(
        defaultImageSaver: DefaultImageSaver
    ): ImageSaver
}