package io.github.alxiw.punkapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.github.alxiw.punkapp.domain.GetFavoriteBeersFlowUseCase
import io.github.alxiw.punkapp.domain.GetFavoriteIdsFlowUseCase
import io.github.alxiw.punkapp.domain.ToggleFavoriteUseCase
import io.github.alxiw.punkapp.presentation.beers.model.BeerItemUiModel
import io.github.alxiw.punkapp.presentation.beers.model.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoriteBeersFlowUseCase: GetFavoriteBeersFlowUseCase,
    getFavoriteIdsFlowUseCase: GetFavoriteIdsFlowUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    internal val favoritesFlow: Flow<PagingData<BeerItemUiModel>> = getFavoriteBeersFlowUseCase()
        .map { pagingData ->
            pagingData.map { beer -> beer.toItemUiModel() }
        }
        .cachedIn(viewModelScope)

    internal val favoriteIds: StateFlow<Set<Int>> = getFavoriteIdsFlowUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    fun onFavoriteClick(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleFavoriteUseCase(id)
        }
    }
}
