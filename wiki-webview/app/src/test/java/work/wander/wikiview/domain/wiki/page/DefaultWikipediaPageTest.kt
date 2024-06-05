package work.wander.wikiview.domain.wiki.page

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.Response
import work.wander.wikiview.data.wiki.WikipediaDatabase
import work.wander.wikiview.data.wiki.entity.WikiPageDefaultHtml
import work.wander.wikiview.data.wiki.entity.WikiPageMobileHtml
import work.wander.wikiview.framework.clock.TestClock
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaDefaultHtmlService
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaMobileHtmlService
import java.time.Instant

class DefaultWikipediaPageTest {

    private val wikipediaMobileHtmlService: WikipediaMobileHtmlService = mockk()
    private val wikipediaDefaultHtmlService: WikipediaDefaultHtmlService = mockk()
    private val wikipediaDatabase: WikipediaDatabase = mockk()
    private val appLogger: AppLogger = mockk()
    private val testClock = TestClock(Instant.now().toEpochMilli())

    private val defaultWikipediaPage = DefaultWikipediaPage(
        wikipediaMobileHtmlService,
        wikipediaDefaultHtmlService,
        wikipediaDatabase,
        appLogger,
        testClock,
    )

    @Test
    fun `getMobileHtmlForPage returns cached page when available and not expired`() = runTest {
        val lastUpdated = Instant.now().minusSeconds(1)
        testClock.setTime(lastUpdated.toEpochMilli())

        val pageTitle = "Test Page"
        val cachedPage = WikiPageMobileHtml(pageTitle, "Test HTML", lastUpdated)

        coEvery {
            wikipediaDatabase.pageHtmlDao().getMobileHtmlForPage(pageTitle)
        } returns cachedPage

        val result = defaultWikipediaPage.getMobileHtmlForPage(pageTitle)

        assertThat(result).isEqualTo(WikipediaPage.MobileHtmlPage(pageTitle, cachedPage.html))
    }

    @Test
    fun `getMobileHtmlForPage fetches and caches page when not available in cache`() = runTest {
        val lastUpdated = Instant.now().minusSeconds(1).toEpochMilli()
        testClock.setTime(lastUpdated)

        val pageTitle = "Test Page"
        val responseBody = "Test HTML"

        coEvery { wikipediaDatabase.pageHtmlDao().getMobileHtmlForPage(pageTitle) } returns null
        coEvery { wikipediaMobileHtmlService.getHtmlPageForTitle(pageTitle) } returns Response.success(
            responseBody
        )
        coEvery { wikipediaDatabase.pageHtmlDao().insertMobileHtmlForPage(any()) } returns 1

        val result = defaultWikipediaPage.getMobileHtmlForPage(pageTitle)

        assertThat(result).isEqualTo(WikipediaPage.MobileHtmlPage(pageTitle, responseBody))
        coVerify {
            wikipediaDatabase.pageHtmlDao().insertMobileHtmlForPage(
                WikiPageMobileHtml(
                    pageTitle,
                    responseBody,
                    Instant.ofEpochMilli(lastUpdated)
                )
            )
        }
    }

    @Test
    fun `getDefaultHtmlForPage returns cached page when available and not expired`() = runTest {
        val lastUpdated = Instant.now().minusSeconds(1).toEpochMilli()
        testClock.setTime(lastUpdated)

        val pageTitle = "Test Page"
        val cachedPage =
            WikiPageDefaultHtml(pageTitle, "Test HTML", Instant.ofEpochMilli(lastUpdated))

        coEvery {
            wikipediaDatabase.pageHtmlDao().getDefaultHtmlForPage(pageTitle)
        } returns cachedPage

        val result = defaultWikipediaPage.getDefaultHtmlForPage(pageTitle)

        assertThat(result).isEqualTo(WikipediaPage.DefaultHtmlPage(pageTitle, cachedPage.html))
    }

    @Test
    fun `getDefaultHtmlForPage fetches and caches page when not available in cache`() = runTest {
        val lastUpdated = Instant.now().minusSeconds(1).toEpochMilli()
        testClock.setTime(lastUpdated)

        val pageTitle = "Test Page"
        val responseBody = "Test HTML"

        coEvery { wikipediaDatabase.pageHtmlDao().getDefaultHtmlForPage(pageTitle) } returns null
        coEvery { wikipediaDefaultHtmlService.getHtmlPageForTitle(pageTitle) } returns Response.success(
            responseBody
        )
        coEvery { wikipediaDatabase.pageHtmlDao().insertDefaultHtmlForPage(any()) } returns 1

        val result = defaultWikipediaPage.getDefaultHtmlForPage(pageTitle)

        assertThat(result).isEqualTo(WikipediaPage.DefaultHtmlPage(pageTitle, responseBody))
        coVerify {
            wikipediaDatabase.pageHtmlDao().insertDefaultHtmlForPage(
                WikiPageDefaultHtml(
                    pageTitle,
                    responseBody,
                    Instant.ofEpochMilli(lastUpdated)
                )
            )
        }
    }
}