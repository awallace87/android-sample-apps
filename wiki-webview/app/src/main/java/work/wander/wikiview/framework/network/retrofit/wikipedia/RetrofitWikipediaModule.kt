package work.wander.wikiview.framework.network.retrofit.wikipedia

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitWikipediaModule {

    @Provides
    @Singleton
    fun provideRetrofitWikipediaSearchService(
        okHttpClient: OkHttpClient
    ): WikipediaSearchService {

        val networkJson = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/w/rest.php/v1/")
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
            .create(WikipediaSearchService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitWikipediaMobileHtmlService(
        okHttpClient: OkHttpClient
    ): WikipediaMobileHtmlService {
        return Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/api/rest_v1/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(WikipediaMobileHtmlService::class.java)
    }

}