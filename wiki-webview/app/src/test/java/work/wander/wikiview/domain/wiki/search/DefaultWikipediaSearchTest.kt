package work.wander.wikiview.domain.wiki.search

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response
import work.wander.wikiview.data.wiki.WikipediaDatabase
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaSearchRetrofitService

class DefaultWikipediaSearchTest {

    private val wikipediaSearchRetrofitService = mockk<WikipediaSearchRetrofitService>()
    private val wikipediaDatabase = mockk<WikipediaDatabase>()
    private val scheduler = TestCoroutineScheduler()
    private val backgroundDispatcher = StandardTestDispatcher(scheduler)
    private val wikiSearchCoroutine = TestScope(backgroundDispatcher)
    private val appLogger = mockk<AppLogger>()

    private val defaultWikipediaSearch = DefaultWikipediaSearch(
        wikipediaSearchRetrofitService,
        wikipediaDatabase,
        backgroundDispatcher,
        wikiSearchCoroutine,
        appLogger
    )

    @Test
    fun `searchForPages returns success when response is successful`() = runTest {
        val query = "Test Query"
        val searchResult = WikipediaSearchRetrofitService.WikipediaSearchResult(
            id = 1,
            key = "Test Key",
            title = "Test Title",
            description = "Test Description",
            thumbnail = null,
            excerpt = "Test Excerpt",
        )
        val responseBody = WikipediaSearchRetrofitService.WikipediaSearchResponse(listOf(searchResult))
        val response = Response.success(responseBody)

        coEvery { wikipediaSearchRetrofitService.search(query) } returns response

        val result = defaultWikipediaSearch.searchForPages(query)

        assertThat(result).isEqualTo(WikipediaSearch.SearchResponse.Success(listOf(
            WikipediaSearch.WikiPageSearchResult(
                wikiPageId = searchResult.id,
                key = searchResult.key,
                title = searchResult.title,
                description = searchResult.description ?: "",
                url = searchResult.thumbnail?.url
            )
        )))
    }

    @Test
    fun `searchForPages returns error when response is not successful`() = runTest {
        val query = "Test Query"
        val response = Response.error<WikipediaSearchRetrofitService.WikipediaSearchResponse>(
            500,
            "".toResponseBody(null),
        )

        coEvery { wikipediaSearchRetrofitService.search(query) } returns response

        val result = defaultWikipediaSearch.searchForPages(query)

        assertThat(result).isEqualTo(WikipediaSearch.SearchResponse.Error("Failed to search Wikipedia."))
    }

    @Test
    fun `searchForPages returns error when response body is null`() = runTest {
        val query = "Test Query"
        val response = Response.success<WikipediaSearchRetrofitService.WikipediaSearchResponse>(null)

        coEvery { wikipediaSearchRetrofitService.search(query) } returns response

        val result = defaultWikipediaSearch.searchForPages(query)

        assertThat(result).isEqualTo(WikipediaSearch.SearchResponse.Error("Null body in successful response."))
    }
}