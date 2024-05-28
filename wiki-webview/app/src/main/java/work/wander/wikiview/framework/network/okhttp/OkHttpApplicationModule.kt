package work.wander.wikiview.framework.network.okhttp

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import work.wander.wikiview.framework.logging.AppLogger
import work.wander.wikiview.framework.network.NetworkStatusMonitor
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class CacheNetworkInterceptor

@Qualifier
annotation class ForceCacheApplicationInterceptor

@Module
@InstallIn(SingletonComponent::class)
class OkHttpApplicationModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(
        @CacheNetworkInterceptor cacheNetworkInterceptor: Interceptor,
        @ForceCacheApplicationInterceptor forceCacheApplicationInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(10))
            .addNetworkInterceptor(cacheNetworkInterceptor)
            .addInterceptor(forceCacheApplicationInterceptor)
            .build()
    }

    /**
     * Interceptor that sets the cache duration for network requests.
     *
     * TODO: Allow for customizing the cache duration
     */
    @CacheNetworkInterceptor
    @Provides
    fun provideOkHttpCacheInterceptor() : Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            val cacheControl = CacheControl.Builder()
                .maxAge(10, TimeUnit.MINUTES)
                .build()
            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

    /**
     * Interceptor that forces cache when there is no internet connection.
     */
    @ForceCacheApplicationInterceptor
    @Provides
    fun provideForceCacheApplicationInterceptor(
        networkStatusMonitor: NetworkStatusMonitor,
        appLogger: AppLogger,
    ) : Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()
            if (!networkStatusMonitor.isInternetConnected()) {
                appLogger.warn("No internet connection present, forcing cache for request: ${request.url}")
                builder.cacheControl(CacheControl.FORCE_CACHE);
            }
            chain.proceed(builder.build());
        }
    }
}