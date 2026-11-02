package io.github.alxiw.punkapp.data.remote.interceptor

import android.util.Log
import io.github.alxiw.punkapp.data.remote.NetworkHandler
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OfflineFallbackInterceptor @Inject constructor(
    private val networkHandler: NetworkHandler
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Offline: the cache is the only source, stale up to CACHE_MAX_STALE.
        if (!networkHandler.isConnected) {
            return chain.proceed(cacheOnlyRequest(request))
        }

        val networkResponse = try {
            chain.proceed(request)
        } catch (io: IOException) {
            return staleResponseOrThrow(chain, io)
        }

        if (networkResponse.isUsable()) return networkResponse

        // OkHttp requires the previous body to be released before the chain may start
        // another exchange ("the previous response is still open").
        val code = networkResponse.code
        val message = networkResponse.message
        networkResponse.close()

        // The server answered, but the payload is unusable (5xx or an HTML page served
        // with 200 after the endpoints moved). Stale data is better than an error screen.
        val stale = proceedCacheOnly(chain)
        if (stale != null) {
            Log.d(TAG, "Serving stale cache for ${request.url} after HTTP $code")
            return stale
        }

        throw IOException("HTTP $code $message for ${request.url} (no usable cache)")
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
        } catch (_: IOException) {
            null
        }
        if (stale != null && !stale.isSuccessful) { // 504 Unsatisfiable Request (only-if-cached)
            stale.close()
            return null
        }
        return stale
    }

    companion object {
        private const val TAG = "PunkApp-OfflineFallbackInterceptor"
        private const val CACHE_MAX_STALE = 60 * 60 * 24 * 28 // 28 days
    }
}
