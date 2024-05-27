package work.wander.wikiview.domain.wiki.common

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import work.wander.wikiview.framework.annotation.BackgroundThread
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForWikipedia

@Module
@InstallIn(SingletonComponent::class)
class WikipediaApplicationCommonModule {

    @Provides
    @Singleton
    @ForWikipedia
    fun providesCoroutineScope(
        @BackgroundThread backgroundDispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + backgroundDispatcher)
    }


}