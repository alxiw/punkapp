package io.github.alxiw.punkapp.domain

import androidx.paging.PagingData
import io.github.alxiw.punkapp.domain.model.Beer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteBeersFlowUseCase @Inject constructor(
    private val beersRepository: BeersRepository
) {
    operator fun invoke(): Flow<PagingData<Beer>> {
        return beersRepository.getFavoriteBeersFlow()
    }
}
