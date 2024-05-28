package work.wander.wikiview.domain.wiki.search

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import work.wander.wikiview.data.wiki.WikipediaDatabase
import work.wander.wikiview.data.wiki.entity.WikiPageMetadata
import work.wander.wikiview.domain.wiki.common.ForWikipedia
import work.wander.wikiview.framework.annotation.BackgroundThread
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaSearchRetrofitService
import javax.inject.Inject

class DefaultWikipediaSearch @Inject constructor(
    private val wikipediaSearchRetrofitService: WikipediaSearchRetrofitService,
    private val wikipediaDatabase: WikipediaDatabase,
    @BackgroundThread private val backgroundDispatcher: CoroutineDispatcher,
    @ForWikipedia private val wikiSearchCoroutine: CoroutineScope,
    private val appLogger: AppLogger,
) : WikipediaSearch {

    override suspend fun searchForPages(query: String): WikipediaSearch.SearchResponse {
        val response = wikipediaSearchRetrofitService.search(query)
        if (response.isSuccessful) {
            val body = response.body()
            if (body == null) {
                return WikipediaSearch.SearchResponse.Error("Null body in successful response.")
            } else {
                val results = body.pages.map { searchResult ->
                    WikipediaSearch.WikiPageSearchResult(
                        wikiPageId = searchResult.id,
                        key = searchResult.key,
                        title = searchResult.title,
                        description = searchResult.description ?: "",
                        url = searchResult.thumbnail?.url
                    )
                }
                updateSearchResultsData(body.pages)
                return WikipediaSearch.SearchResponse.Success(results)
            }
        } else {
            return WikipediaSearch.SearchResponse.Error("Failed to search Wikipedia.")
        }
    }

    private fun updateSearchResultsData(results: List<WikipediaSearchRetrofitService.WikipediaSearchResult>) {
        wikiSearchCoroutine.launch(backgroundDispatcher) {
            val metadata = results.map { searchResult ->
                WikiPageMetadata(
                    wikiPageId = searchResult.id,
                    key = searchResult.key,
                    title = searchResult.title,
                    excerpt = searchResult.excerpt,
                    description = searchResult.description,
                )
            }
            try {
                val insertedIds = wikipediaDatabase.metadataDao().insertAll(metadata)
                appLogger.info("Inserted metadata for ${insertedIds.size} wiki pages.")
            } catch (e: Exception) {
                appLogger.error(e, "Failed to insert metadata for wiki pages.")
            }
        }
    }
}