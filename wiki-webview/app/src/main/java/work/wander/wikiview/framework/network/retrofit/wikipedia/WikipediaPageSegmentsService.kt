package work.wander.wikiview.framework.network.retrofit.wikipedia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaPageSegmentsService {

    @GET("page/segments/{title}")
    suspend fun getSegmentsForTitle(
        @Path("title") pageTitle: String
    ): Response<WikipediaPageSegmentsResponse>

    @Serializable
    data class WikipediaPageSegmentsResponse(
        val sourceLanguage: String,
        @SerialName("title") val pageTitle: String,
        val revision: String,
        @SerialName("segmentedContent") val segmentedContent: String
    )
}