package work.wander.wikiview.framework.network.retrofit.wikipedia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for Wikipedia Search
 */
interface WikipediaSearchRetrofitService {

    /**
     * Search Wikipedia for a given query
     */
    @GET("search/page?")
    suspend fun search(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
    ): Response<WikipediaSearchResponse>


    @Serializable
    data class WikipediaSearchResponse(
        val pages: List<WikipediaSearchResult>
    )

    /**
     * Wikipedia search result
     *
     * @property id The Wikipedia page ID
     * @property key The Wikipedia page key
     * @property title The title of the Wikipedia page
     * @property excerpt An excerpt from the Wikipedia page
     * @property description A description of the Wikipedia page
     * @property thumbnail The thumbnail of the Wikipedia page
     */
    @Serializable
    data class WikipediaSearchResult(
        val id: Long,
        val key: String,
        val title: String,
        val excerpt: String?,
        val description: String?,
        val thumbnail: WikipediaSearchResultThumbnail?
    )

    /**
     * Wikipedia search result thumbnail
     *
     * @property mimeType The MIME type of the thumbnail
     * @property url The URL of the thumbnail
     * @property width The width of the thumbnail
     * @property height The height of the thumbnail
     */
    @Serializable
    data class WikipediaSearchResultThumbnail(
        @SerialName("mimetype") val mimeType: String?,
        val url: String,
        val width: Int,
        val height: Int,
    )

}