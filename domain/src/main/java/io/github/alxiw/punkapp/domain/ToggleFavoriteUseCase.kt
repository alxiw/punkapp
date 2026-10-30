package io.github.alxiw.punkapp.domain

import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val beersRepository: BeersRepository
) {
    suspend operator fun invoke(id: Int) {
        beersRepository.toggleFavorite(id)
    }
}
