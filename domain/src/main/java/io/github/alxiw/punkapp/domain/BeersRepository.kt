package io.github.alxiw.punkapp.domain

import androidx.paging.PagingData
import io.github.alxiw.punkapp.domain.model.Beer
import kotlinx.coroutines.flow.Flow

interface BeersRepository {
    fun getBeersFlow(query: String?): Flow<PagingData<Beer>>
    fun getFavoriteBeersFlow(): Flow<PagingData<Beer>>
    fun getFavoriteIdsFlow(): Flow<Set<Int>>
    suspend fun getBeer(id: Int): Result<Beer>
    suspend fun toggleFavorite(id: Int)
}
