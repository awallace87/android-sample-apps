package work.wander.wikiview.domain.wiki.search

interface WikipediaSearch {

    /**
     * Search for Wikipedia pages with the given query.
     */
    suspend fun searchForPages(query: String): SearchResponse

    /**
     * Data class representing a search result from Wikipedia.
     */
    data class WikiPageSearchResult(
        val wikiPageId: Long,
        val title: String,
        val key: String,
        val description: String,
        val url: String?,
    )

    sealed interface SearchResponse {
        data class Success(val pages: List<WikiPageSearchResult>) : SearchResponse
        data class Error(val message: String) : SearchResponse
    }
}