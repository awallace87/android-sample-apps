package work.wander.wikiview.framework.network.retrofit.wikipedia

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaMobileHtmlService {

    /**
     * Get the mobile HTML for a Wikipedia page
     */
    @GET("page/mobile-html/{title}")
    suspend fun getHtmlPageForTitle(
        @Path("title") pageTitle: String
    ): Response<String>
}