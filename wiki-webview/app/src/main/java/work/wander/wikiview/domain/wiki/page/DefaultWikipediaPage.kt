package work.wander.wikiview.domain.wiki.page

import work.wander.wikiview.data.wiki.WikipediaDatabase
import work.wander.wikiview.data.wiki.entity.WikiPageMobileHtml
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaDefaultHtmlService
import work.wander.wikiview.framework.network.retrofit.wikipedia.WikipediaMobileHtmlService
import javax.inject.Inject

class DefaultWikipediaPage @Inject constructor(
    private val wikipediaMobileHtmlService: WikipediaMobileHtmlService,
    private val wikipediaDefaultHtmlService: WikipediaDefaultHtmlService,
    private val wikipediaDatabase: WikipediaDatabase,
    private val appLogger: AppLogger,
) : WikipediaPage {

    override suspend fun getMobileHtmlForPage(pageTitle: String): WikipediaPage.MobileHtmlPage? {
        val cachedPage = wikipediaDatabase.pageHtmlDao().getMobileHtmlForPage(pageTitle)
        // TODO: Add in expiration logic for cached pages
        return if (cachedPage != null) {
            WikipediaPage.MobileHtmlPage(pageTitle, cachedPage.html)
        } else {
            fetchAndCachePage(pageTitle)
        }
    }

    override suspend fun getDefaultHtmlForPage(pageTitle: String): WikipediaPage.DefaultHtmlPage? {
        val remotePage = wikipediaDefaultHtmlService.getHtmlPageForTitle(pageTitle)
        return if (remotePage.isSuccessful) {
            val responseBody = remotePage.body()
            if (responseBody == null) {
                appLogger.error("Response body is null for default HTML request for $pageTitle.")
                null
            } else {
                WikipediaPage.DefaultHtmlPage(pageTitle, responseBody)
            }
        } else {
            appLogger.error("Failed to fetch HTML for page $pageTitle.")
            null
        }
    }

    private suspend fun fetchAndCachePage(pageTitle: String): WikipediaPage.MobileHtmlPage? {
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
}