package io.github.alxiw.punkapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.alxiw.punkapp.data.local.BeersLocalDataSource
import io.github.alxiw.punkapp.data.remote.model.toBeer
import io.github.alxiw.punkapp.domain.model.Beer
import io.github.alxiw.punkapp.domain.model.BeersFilter

/**
 * @param ids `null` - no restriction by ids; an empty list - "favorites only, but there are none",
 * which gives an empty list without any network request.
 */
internal class BeerPagingSource(
    private val filter: BeersFilter,
    private val ids: List<Int>?,
    private val remoteDataSource: BeersRemoteDataSource,
    private val localDataSource: BeersLocalDataSource
) : PagingSource<Int, Beer>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Beer> {
        if (ids != null && ids.isEmpty()) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        val page = params.key ?: 1

        return remoteDataSource.getBeers(filter, ids, page).fold(
            onSuccess = { data ->
                val favorites = localDataSource.getFavoriteIds()
                val beers = data.map {
                    it.toBeer().copy(isFavorite = favorites.contains(it.id))
                }
                // The API fills a page up to `per_page`, so a short page means the data is exhausted
                val isLastPage = data.size < BeersRemoteDataSource.PAGE_SIZE
                LoadResult.Page(
                    data = beers,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (isLastPage) null else page + 1
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
