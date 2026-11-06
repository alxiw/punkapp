package io.github.alxiw.punkapp.data.remote.util

import android.content.Context
import android.os.StatFs
import java.io.File

private const val MIN_DISK_CACHE_SIZE = 1 * 1024 * 1024 // 1MB
private const val MAX_DISK_CACHE_SIZE = 15 * 1024 * 1024 // 8MB
private const val CACHE_NAME = "punkapp_json_cache"

internal object CacheUtil {

    fun createDefaultCacheDir(context: Context): File {
        val cache = File(context.applicationContext.cacheDir, CACHE_NAME)
        if (!cache.exists()) {
            cache.mkdirs()
        }
        return cache
    }

    fun calculateDiskCacheSize(dir: File): Long {
        val calculatedSize = try {
            val statFs = StatFs(dir.absolutePath)
            (statFs.blockCountLong * statFs.blockSizeLong) / 50
        } catch (_: IllegalArgumentException) {
            MIN_DISK_CACHE_SIZE.toLong()
        }

        return calculatedSize.coerceIn(MIN_DISK_CACHE_SIZE.toLong(), MAX_DISK_CACHE_SIZE.toLong())
    }
}
