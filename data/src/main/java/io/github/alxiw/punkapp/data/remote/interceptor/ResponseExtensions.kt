package io.github.alxiw.punkapp.data.remote.interceptor

import okhttp3.Response

private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val JSON_MIME_TYPE = "application/json"

internal fun Response.isJson(): Boolean {
    // A missing header keeps the previous behaviour instead of guessing.
    val contentType = header(CONTENT_TYPE_HEADER) ?: return true
    return contentType.startsWith(JSON_MIME_TYPE, ignoreCase = true)
}

internal fun Response.isUsable(): Boolean = isSuccessful && isJson()
