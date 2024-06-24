package work.wander.funnyface.framework.network.retrofit.wikipedia

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaDefaultHtmlService {

    @GET("page/html/{title}")
    suspend fun getHtmlPageForTitle(
        @Path("title") pageTitle: String
    ): Response<String>
}