package work.wander.funnyface.domain.image

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class ForOverlayImages

@Module
@InstallIn(SingletonComponent::class)
class OverlayImageSaverModule {

    @Provides
    @ForOverlayImages
    @Singleton
    fun provideOverlayImageOutputDirectory(
        @ApplicationContext context: Context
    ) : File {
        return context.filesDir.resolve("overlay_images").apply {
            mkdirs()
        }
    }
}