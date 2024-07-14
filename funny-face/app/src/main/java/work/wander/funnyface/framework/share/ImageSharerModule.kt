package work.wander.funnyface.framework.share

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageSharerModule {

    @Binds
    abstract fun bindImageSharer(
        defaultImageSharer: DefaultImageSharer
    ): ImageSharer

}