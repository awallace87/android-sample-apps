package work.wander.wikiview.domain.wiki.page

import android.util.Base64

interface WikipediaPage {

    data class MobileHtmlPage(
        val pageTitle: String,
        val html: String
    ) {
        /**
         * Get the HTML as a base64 encoded string. Necessary for displaying the HTML in a WebView.loadData() call.
         */
        fun getBase64EncodedHtml(): String {
            return Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
        }
    }

    suspend fun getMobileHtmlForPage(pageTitle: String): MobileHtmlPage?

    data class DefaultHtmlPage(
        val pageTitle: String,
        val html: String
    ) {
        /**
         * Get the HTML as a base64 encoded string. Necessary for displaying the HTML in a WebView.loadData() call.
         */
        fun getBase64EncodedHtml(): String {
            return Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
        }
    }

    suspend fun getDefaultHtmlForPage(pageTitle: String): DefaultHtmlPage?
}