package io.github.alxiw.punkapp.data.remote

import io.github.alxiw.punkapp.data.remote.api.BeersSearchService
import io.github.alxiw.punkapp.data.remote.model.BeerResponse
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BeersRemoteDataSource @Inject constructor(
    private val service: BeersSearchService
) {

    suspend fun getBeers(
        query: String?,
        page: Int
    ): Result<List<BeerResponse>> = runCatching {
        val beerName = if (query.isNullOrEmpty()) null else query.replace(' ', '_')
        val response = service.getBeers(beerName, page)
        response.body()?.takeIf { response.isSuccessful }
            ?: throw IOException("Error ${response.code()} ${response.message()}")
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(IOException("Network error while getting beers data by page $page", it)) }
    )

    suspend fun getBeersByIds(
        ids: List<Int>,
        page: Int
    ): Result<List<BeerResponse>> = runCatching {
        val idsString = ids.joinToString(",")
        val response = service.getBeersByIds(idsString, page)
        response.body()?.takeIf { response.isSuccessful }
            ?: throw IOException("Error ${response.code()} ${response.message()}")
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(IOException("Network error while getting beers data by ids $ids and page $page", it)) }
    )

    suspend fun getBeer(
        id: Int
    ): Result<BeerResponse> = runCatching {
        val response = service.getBeer(id)
        response.body()?.takeIf { response.isSuccessful }
            ?: throw IOException("Error ${response.code()} ${response.message()}")
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(IOException("Network error while getting beer data by id $id", it)) }
    )
}
