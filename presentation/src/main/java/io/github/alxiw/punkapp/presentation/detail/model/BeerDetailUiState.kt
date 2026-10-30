package io.github.alxiw.punkapp.presentation.detail.model

internal sealed interface BeerDetailUiState {
    data object Loading : BeerDetailUiState
    data class Success(val model: BeerDetailUiModel) : BeerDetailUiState
    data class Error(val exception: Throwable) : BeerDetailUiState
}
