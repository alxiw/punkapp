package io.github.alxiw.punkapp.data.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import io.github.alxiw.punkapp.data.remote.api.BeersSearchService
import io.github.alxiw.punkapp.data.remote.NetworkHandler
import io.github.alxiw.punkapp.data.remote.interceptor.CacheControlInterceptor
import io.github.alxiw.punkapp.data.remote.interceptor.OfflineFallbackInterceptor
import io.github.alxiw.punkapp.data.remote.util.CacheUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val API_LOG_TAG = "PunkApp-ApiLoggingInterceptor"
    private const val IMAGE_LOG_TAG = "PunkApp-ImagesLoggingInterceptor"
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
    fun provideApiHttpClient(
        cache: Cache,
        offlineFallbackInterceptor: OfflineFallbackInterceptor,
        cacheControlInterceptor: CacheControlInterceptor,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor { message -> Log.d(API_LOG_TAG, message) }
            .apply { level = HttpLoggingInterceptor.Level.BASIC }

        return OkHttpClient.Builder()
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor(logging)
            .addInterceptor(offlineFallbackInterceptor)
            .addNetworkInterceptor(cacheControlInterceptor)
            .build()
    }

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
