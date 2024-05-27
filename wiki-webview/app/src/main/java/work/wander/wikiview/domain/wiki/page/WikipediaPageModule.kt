package work.wander.wikiview.domain.wiki.page

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Duration
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PageDuration

@Module
@InstallIn(SingletonComponent::class)
class WikipediaPageModule {

    @Provides
    @Singleton
    @PageDuration
    fun providesPageDuration(): Duration {
        return Duration.ofDays(7)
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