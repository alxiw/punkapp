package io.github.alxiw.punkapp.data.remote

import io.github.alxiw.punkapp.data.remote.api.BeersSearchService
import io.github.alxiw.punkapp.data.remote.model.BeerResponse
import io.github.alxiw.punkapp.domain.model.BeersFilter
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BeersRemoteDataSource @Inject constructor(
    private val service: BeersSearchService
) {

    /**
     * @param ids restrict the result to these beers; `null` means "don't send ids at all".
     * Callers must not pass an empty list - an empty `ids` would be dropped by the API
     * and return every beer.
     */
    suspend fun getBeers(
        filter: BeersFilter,
        ids: List<Int>?,
        page: Int
    ): Result<List<BeerResponse>> = runCatching {
        require(ids == null || ids.isNotEmpty()) { "ids must not be empty" }
        val response = service.getBeers(
            page = page,
            beerName = filter.query.toApiValue(),
            abvGt = filter.abvGt,
            abvLt = filter.abvLt,
            ibuGt = filter.ibuGt,
            ibuLt = filter.ibuLt,
            ebcGt = filter.ebcGt,
            ebcLt = filter.ebcLt,
            food = filter.food?.toApiValue(),
            ids = ids?.joinToString(","),
        )
        response.body()?.takeIf { response.isSuccessful }
            ?: throw IOException("Error ${response.code()} ${response.message()}")
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(IOException("Network error while getting beers data by page $page", it)) }
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

    // The API expects words joined with underscores; empty values are not sent at all
    private fun String.toApiValue(): String? = if (isEmpty()) null else replace(' ', '_')
}
