package io.github.alxiw.punkapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.alxiw.punkapp.data.local.BeersLocalDataSource
import io.github.alxiw.punkapp.data.remote.model.toBeer
import io.github.alxiw.punkapp.domain.model.Beer

internal class BeerPagingSource(
    private val query: String?,
    private val remoteDataSource: BeersRemoteDataSource,
    private val localDataSource: BeersLocalDataSource
) : PagingSource<Int, Beer>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Beer> {
        val page = params.key ?: 1

        return remoteDataSource.getBeers(query, page).fold(
            onSuccess = { data ->
                val favorites = localDataSource.getFavoriteIds()
                val beers = data.map { 
                    it.toBeer().copy(isFavorite = favorites.contains(it.id))
                }
                LoadResult.Page(
                    data = beers,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (beers.isEmpty()) null else page + 1
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Beer>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
