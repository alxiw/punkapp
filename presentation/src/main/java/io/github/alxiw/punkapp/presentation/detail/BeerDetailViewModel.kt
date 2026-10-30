package io.github.alxiw.punkapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.alxiw.punkapp.domain.GetBeerUseCase
import io.github.alxiw.punkapp.domain.ToggleFavoriteUseCase
import io.github.alxiw.punkapp.presentation.detail.model.BeerDetailUiState
import io.github.alxiw.punkapp.presentation.detail.model.toDetailUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBeer: GetBeerUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BeerDetailUiState>(BeerDetailUiState.Loading)
    internal val uiState: StateFlow<BeerDetailUiState> = _uiState.asStateFlow()

    private var beerId: Int = requireNotNull(savedStateHandle["beerId"]) {
        "BeerDetailViewModel requires beerId in SavedStateHandle"
    }

    init {
        loadBeer(beerId)
    }

    private fun loadBeer(beerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = getBeer(beerId).fold(
                onSuccess = { BeerDetailUiState.Success(it.toDetailUiModel()) },
                onFailure = { BeerDetailUiState.Error(it) }
            )
        }
    }

    fun retry() {
        loadBeer(beerId)
    }

    fun onFavoriteClick() {
        val currentState = _uiState.value
        if (currentState is BeerDetailUiState.Success) {
            val newFavoriteStatus = !currentState.model.isFavorite
            _uiState.update {
                if (it is BeerDetailUiState.Success) {
                    it.copy(model = it.model.copy(isFavorite = newFavoriteStatus))
                } else it
            }

            viewModelScope.launch(Dispatchers.IO) {
                toggleFavoriteUseCase(beerId)
            }
        }
    }
}
