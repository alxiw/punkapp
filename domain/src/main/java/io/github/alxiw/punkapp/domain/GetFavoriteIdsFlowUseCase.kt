package io.github.alxiw.punkapp.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteIdsFlowUseCase @Inject constructor(
    private val beersRepository: BeersRepository
) {
    operator fun invoke(): Flow<Set<Int>> {
        return beersRepository.getFavoriteIdsFlow()
    }
}
