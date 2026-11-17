package io.github.alxiw.punkapp.domain

import androidx.paging.PagingData
import io.github.alxiw.punkapp.domain.model.Beer
import io.github.alxiw.punkapp.domain.model.BeersFilter
import kotlinx.coroutines.flow.Flow

interface BeersRepository {
    fun getBeersFlow(filter: BeersFilter): Flow<PagingData<Beer>>
    fun getFavoriteIdsFlow(): Flow<Set<Int>>
    suspend fun getBeer(id: Int): Result<Beer>
    suspend fun toggleFavorite(id: Int)
}
