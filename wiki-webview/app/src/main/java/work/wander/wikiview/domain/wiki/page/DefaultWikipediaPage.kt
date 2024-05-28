package work.wander.wikiview.domain.wiki.page

import work.wander.wikiview.data.wiki.WikipediaDatabase
import work.wander.wikiview.data.wiki.entity.WikiPageDefaultHtml
import work.wander.wikiview.data.wiki.entity.WikiPageMobileHtml
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaDefaultHtmlService
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaMobileHtmlService
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class DefaultWikipediaPage @Inject constructor(
    private val wikipediaMobileHtmlService: WikipediaMobileHtmlService,
    private val wikipediaDefaultHtmlService: WikipediaDefaultHtmlService,
    private val wikipediaDatabase: WikipediaDatabase,
    @PageDuration private val pageDuration: Duration,
    private val appLogger: AppLogger,
) : WikipediaPage {

    override suspend fun getMobileHtmlForPage(pageTitle: String): WikipediaPage.MobileHtmlPage? {
        val cachedPage = wikipediaDatabase.pageHtmlDao().getMobileHtmlForPage(pageTitle)
        return if (cachedPage != null) {
            if (cachedPage.lastUpdated.plus(pageDuration).isBefore(Instant.now())) {
                fetchAndCacheMobileHtmlPage(pageTitle)
            } else {
                WikipediaPage.MobileHtmlPage(pageTitle, cachedPage.html)
            }
        } else {
            fetchAndCacheMobileHtmlPage(pageTitle)
        }
    }

    override suspend fun getDefaultHtmlForPage(pageTitle: String): WikipediaPage.DefaultHtmlPage? {
        val cachedPage = wikipediaDatabase.pageHtmlDao().getDefaultHtmlForPage(pageTitle)
        return if (cachedPage != null) {
            if (cachedPage.lastUpdated.plus(pageDuration).isBefore(Instant.now())) {
                fetchAndCacheDefaultHtmlPage(pageTitle)
            } else {
                WikipediaPage.DefaultHtmlPage(pageTitle, cachedPage.html)
            }
        } else {
            fetchAndCacheDefaultHtmlPage(pageTitle)
        }
    }

    private suspend fun fetchAndCacheMobileHtmlPage(pageTitle: String): WikipediaPage.MobileHtmlPage? {
        wikipediaMobileHtmlService.getHtmlPageForTitle(pageTitle).let { response ->
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody == null) {
                    null
                } else {
                    val page = WikipediaPage.MobileHtmlPage(pageTitle, responseBody)
                    wikipediaDatabase.pageHtmlDao()
                        .insertMobileHtmlForPage(WikiPageMobileHtml(pageTitle, responseBody))
                    page
                }
            } else {
                appLogger.error("Failed to fetch HTML for page $pageTitle.")
                null
            }
        }
    }

    private suspend fun fetchAndCacheDefaultHtmlPage(pageTitle: String): WikipediaPage.DefaultHtmlPage? {
        val remotePage = wikipediaDefaultHtmlService.getHtmlPageForTitle(pageTitle)
        return if (remotePage.isSuccessful) {
            val responseBody = remotePage.body()
            if (responseBody == null) {
                appLogger.error("Response body is null for default HTML request for $pageTitle.")
                null
            } else {
                val page = WikipediaPage.DefaultHtmlPage(pageTitle, responseBody)
                wikipediaDatabase.pageHtmlDao()
                    .insertDefaultHtmlForPage(WikiPageDefaultHtml(pageTitle, responseBody))
                page
            }
        } else {
            appLogger.error("Failed to fetch HTML for page $pageTitle.")
            null
        }
    }
}