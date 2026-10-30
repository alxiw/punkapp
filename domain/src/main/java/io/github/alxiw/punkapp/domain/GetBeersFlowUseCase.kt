package io.github.alxiw.punkapp.domain

import androidx.paging.PagingData
import io.github.alxiw.punkapp.domain.model.Beer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBeersFlowUseCase @Inject constructor(
    private val beersRepository: BeersRepository
) {
    operator fun invoke(query: String?): Flow<PagingData<Beer>> {
        return beersRepository.getBeersFlow(query)
    }
}
