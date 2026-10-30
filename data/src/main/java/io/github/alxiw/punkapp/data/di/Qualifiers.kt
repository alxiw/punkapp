package io.github.alxiw.punkapp.data.di

import javax.inject.Qualifier

/** Retrofit client: HTTP cache, stale fallback, JSON-only responses. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiHttpClient

/** Plain client for Coil: no API cache policy and no stale fallback. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ImageHttpClient
