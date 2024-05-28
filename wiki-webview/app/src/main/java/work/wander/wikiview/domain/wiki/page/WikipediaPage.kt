package work.wander.wikiview.domain.wiki.page

import android.util.Base64

/**
 * Interface for accessing Wikipedia page data (in HTML format).
 */
interface WikipediaPage {

    /**
     * Represents the mobile HTML of a Wikipedia page.
     *
     * @property pageTitle the title of the Wikipedia page
     * @property html the mobile HTML of the Wikipedia page
     */
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

    /**
     * Represents the default HTML of a Wikipedia page.
     *
     * @property pageTitle the title of the Wikipedia page
     * @property html the default HTML of the Wikipedia page
     */
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