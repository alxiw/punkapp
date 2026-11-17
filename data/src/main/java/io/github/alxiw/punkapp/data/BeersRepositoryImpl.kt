package io.github.alxiw.punkapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.alxiw.punkapp.data.local.BeersLocalDataSource
import io.github.alxiw.punkapp.data.remote.BeerPagingSource
import io.github.alxiw.punkapp.data.remote.BeersRemoteDataSource
import io.github.alxiw.punkapp.data.remote.model.toBeer
import io.github.alxiw.punkapp.domain.BeersRepository
import io.github.alxiw.punkapp.domain.model.Beer
import io.github.alxiw.punkapp.domain.model.BeersFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BeersRepositoryImpl @Inject constructor(
    private val remoteDataSource: BeersRemoteDataSource,
    private val localDataSource: BeersLocalDataSource
) : BeersRepository {

    override fun getBeersFlow(filter: BeersFilter): Flow<PagingData<Beer>> = flow {
        // Favorite ids are read once per Pager, so the set stays stable while the user paginates
        // and toggling a heart doesn't restart paging
        val ids = if (filter.favoritesOnly) localDataSource.getFavoriteIds() else null
        emitAll(
            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { BeerPagingSource(filter, ids, remoteDataSource, localDataSource) }
            ).flow
        )
    }

    override fun getFavoriteIdsFlow(): Flow<Set<Int>> = localDataSource.getFavoriteIdsFlow()
        .map { it.toSet() }

    override suspend fun getBeer(id: Int): Result<Beer> {
        return remoteDataSource.getBeer(id).map { it.toBeer().copy(isFavorite = localDataSource.isFavorite(id)) }
    }

    override suspend fun toggleFavorite(id: Int) {
        if (localDataSource.isFavorite(id)) {
            localDataSource.removeFavorite(id)
        } else {
            localDataSource.addFavorite(id)
        }
    }

    companion object {
        private const val PAGE_SIZE = 30
    }
}
