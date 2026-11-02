package io.github.alxiw.punkapp.data.remote.interceptor

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CacheControlInterceptor @Inject constructor() : Interceptor {

    private val freshCacheControl = CacheControl.Builder()
        .maxAge(CACHE_MAX_AGE, TimeUnit.SECONDS)
        .build()
        .toString()

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return when {
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

    companion object {
        private const val CACHE_MAX_AGE = 60 * 60 * 1 * 1 // 60 minutes
        private const val CACHE_HEADER = "Cache-Control"
        private const val PRAGMA_HEADER = "Pragma"
        private const val EXPIRES_HEADER = "Expires"
        private const val NO_STORE = "no-store"
    }
}
