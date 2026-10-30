package io.github.alxiw.punkapp.domain

import io.github.alxiw.punkapp.domain.model.Beer
import javax.inject.Inject

class GetBeerUseCase @Inject constructor(
    private val beersRepository: BeersRepository
) {
    suspend operator fun invoke(id: Int): Result<Beer> {
        return beersRepository.getBeer(id)
    }
}
