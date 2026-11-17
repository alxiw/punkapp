package io.github.alxiw.punkapp.domain

import androidx.paging.PagingData
import io.github.alxiw.punkapp.domain.model.Beer
import io.github.alxiw.punkapp.domain.model.BeersFilter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBeersFlowUseCase @Inject constructor(
    private val beersRepository: BeersRepository
) {
    operator fun invoke(filter: BeersFilter): Flow<PagingData<Beer>> {
        return beersRepository.getBeersFlow(filter)
    }
}
