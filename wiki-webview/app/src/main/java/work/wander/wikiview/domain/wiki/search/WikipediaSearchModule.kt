package work.wander.wikiview.domain.wiki.search

import dagger.Binds
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



@Module
@InstallIn(SingletonComponent::class)
class WikipediaSearchModule {



}

@Module
@InstallIn(SingletonComponent::class)
abstract class WikipediaSearchBindsModule {

    @Binds
    @Singleton
    abstract fun bindWikipediaSearch(
        defaultWikipediaSearch: DefaultWikipediaSearch
    ): WikipediaSearch

}