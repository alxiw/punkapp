package io.github.alxiw.punkapp.data.remote.api

import android.net.Uri
import io.github.alxiw.punkapp.data.remote.model.BeerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface BeersSearchService {

    /** Every nullable query parameter is omitted from the url when it is `null`. */
    @GET("beers")
    suspend fun getBeers(
        @Query("page") page: Int,
        @Query("beer_name") beerName: String?,
        @Query("abv_gt") abvGt: Double?,
        @Query("abv_lt") abvLt: Double?,
        @Query("ibu_gt") ibuGt: Double?,
        @Query("ibu_lt") ibuLt: Double?,
        @Query("ebc_gt") ebcGt: Double?,
        @Query("ebc_lt") ebcLt: Double?,
        @Query("brewed_after") brewedAfter: String?,
        @Query("brewed_before") brewedBefore: String?,
        @Query("food") food: String?,
        @Query("ids", encoded = true) ids: String?,
    ): Response<List<BeerResponse>>

    @GET("beers/{id}")
    suspend fun getBeer(
        @Path("id") id: Int
    ): Response<BeerResponse>

    companion object {
        val ENDPOINT = Uri.Builder().scheme("https")
            .authority("punkapi.dev")
            .appendEncodedPath("api/v3/")
            .build()
            .toString()
    }
}
