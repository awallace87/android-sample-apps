package work.wander.directory.framework.network.okhttp

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import work.wander.directory.framework.logging.AppLogger
import java.time.Duration
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Annotation for OkHttp logging dependencies.
 **/
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ForOkHttpLogging

/**
 * Dagger module for providing OkHttp related dependencies.
 *
 * This class provides methods for creating an OkHttpClient and a network logging interceptor.
 * The OkHttpClient is configured with a call timeout and the network logging interceptor.
 * The network logging interceptor logs the request and response details using the AppLogger.
 *
 * The @Module and @InstallIn annotations indicate that this is a Dagger module and it should be installed in the SingletonComponent.
 * This means that the provided dependencies will be singletons.
 *
 * @see providesOkHttpClient for providing an OkHttpClient.
 * @see providesNetworkLoggingInterceptor for providing a network logging interceptor.
 */
@Module
@InstallIn(SingletonComponent::class)
class OkHttpApplicationModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(
        @ForOkHttpLogging networkLoggingInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(10))
            .addNetworkInterceptor(networkLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @ForOkHttpLogging
    fun providesNetworkLoggingInterceptor(
        appLogger: AppLogger,
    ): Interceptor {
        var requestNum = 0
        return Interceptor { chain ->
            val request = chain.request()
            val requestNumber = requestNum++
            appLogger.debug("OkHttp ($requestNumber) Request: ${request.url}")
            val response = chain.proceed(request)
            if (response.isSuccessful) {
                appLogger.debug("OkHttp ($requestNumber) Success: ${response.code}")
            } else {
                appLogger.error("OkHttp ($requestNumber) Error: ${response.code}")
                appLogger.error("OkHttp ($requestNumber) Error Body: ${response.body?.string()}")
            }
            response
        }
    }
}