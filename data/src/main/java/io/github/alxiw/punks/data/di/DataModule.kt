package io.github.alxiw.punks.data.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import io.github.alxiw.punks.data.remote.api.BeersSearchService
import io.github.alxiw.punks.data.remote.NetworkHandler
import io.github.alxiw.punks.data.remote.util.CacheUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val CACHE_MAX_STALE = 60 * 60 * 24 * 28 // 28 days
    private const val CACHE_MAX_AGE = 60 * 60 * 1 * 1 // 60 minutes
    private const val CACHE_HEADER = "Cache-Control"
    private const val PRAGMA_HEADER = "Pragma"
    private const val EXPIRES_HEADER = "Expires"
    private const val CONTENT_TYPE_HEADER = "Content-Type"
    private const val JSON_MIME_TYPE = "application/json"
    private const val NO_STORE = "no-store"
    private const val API_LOG_TAG = "HELLO-API"
    private const val IMAGE_LOG_TAG = "HELLO-IMAGES"
    private const val TIMEOUT_SECONDS = 15L
    private const val DATA_STORE_FILE_NAME = "punkapp"

    @Provides
    @Singleton
    internal fun provideNetworkHandler(@ApplicationContext context: Context): NetworkHandler {
        return NetworkHandler(context)
    }

    @Provides
    @Singleton
    internal fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheDir: File = CacheUtil.createDefaultCacheDir(context)
        val maxSize = CacheUtil.calculateDiskCacheSize(cacheDir)

        return Cache(cacheDir, maxSize)
    }

    @Provides
    @Singleton
    internal fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    @ApiHttpClient
    fun provideApiHttpClient(cache: Cache, handler: NetworkHandler): OkHttpClient {
        val logging = HttpLoggingInterceptor { message -> Log.d(API_LOG_TAG, message) }
            .apply { level = HttpLoggingInterceptor.Level.BASIC }
        val freshCacheControl = CacheControl.Builder()
            .maxAge(CACHE_MAX_AGE, TimeUnit.SECONDS)
            .build()
            .toString()

        return OkHttpClient.Builder()
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .cache(cache)
            .addInterceptor { chain ->
                val request = chain.request()

                // Offline: the cache is the only source, stale up to CACHE_MAX_STALE.
                if (!handler.isConnected) {
                    return@addInterceptor chain.proceed(cacheOnlyRequest(request))
                }

                val networkResponse = try {
                    chain.proceed(request)
                } catch (io: IOException) {
                    return@addInterceptor staleResponseOrThrow(chain, io)
                }

                if (networkResponse.isUsable()) return@addInterceptor networkResponse

                // OkHttp requires the previous body to be released before the chain may start
                // another exchange ("the previous response is still open").
                val code = networkResponse.code
                val message = networkResponse.message
                networkResponse.close()

                // The server answered, but the payload is unusable (5xx or an HTML page served
                // with 200 after the endpoints moved). Stale data is better than an error screen.
                val stale = proceedCacheOnly(chain)
                if (stale != null) {
                    Log.d(API_LOG_TAG, "Serving stale cache for ${request.url} after HTTP $code")
                    return@addInterceptor stale
                }

                throw IOException("HTTP $code $message for ${request.url} (no usable cache)")
            }
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                when {
                    // 304 must reach CacheInterceptor untouched: on update its headers are
                    // combined into the stored entry, so no-store here would kill the cache.
                    response.code == HTTP_NOT_MODIFIED -> response

                    response.isSuccessful && response.isJson() -> response.newBuilder()
                        .removeHeader(PRAGMA_HEADER)
                        .removeHeader(CACHE_HEADER)
                        .removeHeader(EXPIRES_HEADER)
                        .header(CACHE_HEADER, freshCacheControl)
                        .build()

                    // Never let an error page (e.g. index.html served with 200) overwrite
                    // good data in the cache.
                    else -> response.newBuilder()
                        .header(CACHE_HEADER, NO_STORE)
                        .build()
                }
            }
            .build()
    }

    private fun cacheOnlyRequest(request: Request): Request = request.newBuilder()
        .cacheControl(
            CacheControl.Builder()
                .onlyIfCached()
                .maxStale(CACHE_MAX_STALE, TimeUnit.SECONDS)
                .build()
        )
        .build()

    private fun staleResponseOrThrow(chain: Interceptor.Chain, cause: IOException): Response {
        if (!chain.call().isCanceled()) {
            proceedCacheOnly(chain)?.let { return it }
        }
        throw cause
    }

    /**
     * Second pass over the same chain: application interceptors are allowed to call proceed()
     * several times. Without network access the cache can answer with a stale entry.
     */
    private fun proceedCacheOnly(chain: Interceptor.Chain): Response? {
        val stale = try {
            chain.proceed(cacheOnlyRequest(chain.request()))
        } catch (ignored: IOException) {
            null
        }
        if (stale != null && !stale.isSuccessful) { // 504 Unsatisfiable Request (only-if-cached)
            stale.close()
            return null
        }
        return stale
    }

    private fun Response.isJson(): Boolean {
        // A missing header keeps the previous behaviour instead of guessing.
        val contentType = header(CONTENT_TYPE_HEADER) ?: return true
        return contentType.startsWith(JSON_MIME_TYPE, ignoreCase = true)
    }

    private fun Response.isUsable(): Boolean = isSuccessful && isJson()

    // Coil caches images on its own disk, so this client needs neither the API cache policy
    // nor the stale fallback - only transport settings and logging.
    @Provides
    @Singleton
    @ImageHttpClient
    fun provideImageHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor { message -> Log.d(IMAGE_LOG_TAG, message) }
            .apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder()
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideBeersSearchService(
        moshi: Moshi,
        @ApiHttpClient client: OkHttpClient
    ): BeersSearchService {
        return Retrofit.Builder()
            .baseUrl(BeersSearchService.ENDPOINT)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BeersSearchService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(DATA_STORE_FILE_NAME) }
        )
    }
}
