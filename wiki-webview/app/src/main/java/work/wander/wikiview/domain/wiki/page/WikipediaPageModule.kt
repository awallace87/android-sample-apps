package work.wander.wikiview.domain.wiki.page

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PageDuration

@Module
@InstallIn(SingletonComponent::class)
class WikipediaPageModule {

    @Provides
    @Singleton
    @PageDuration
    fun providesPageDuration(): Duration {
        return 7.days
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class WikipediaPageBindsModule {

    @Binds
    @Singleton
    abstract fun bindWikipediaPage(
        defaultWikipediaPage: DefaultWikipediaPage
    ): WikipediaPage
}