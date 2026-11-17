package io.github.alxiw.punkapp.presentation.beers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.alxiw.punkapp.domain.GetBeersFlowUseCase
import io.github.alxiw.punkapp.domain.GetFavoriteIdsFlowUseCase
import io.github.alxiw.punkapp.domain.ToggleFavoriteUseCase
import io.github.alxiw.punkapp.domain.model.BeersFilter
import io.github.alxiw.punkapp.presentation.beers.model.BeerItemUiModel
import io.github.alxiw.punkapp.presentation.beers.model.toItemUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class BeersViewModel @Inject constructor(
    private val getBeersFlowUseCase: GetBeersFlowUseCase,
    getFavoriteIdsFlowUseCase: GetFavoriteIdsFlowUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // region Filter inputs: one flow per filtering parameter

    internal val initialQuery: String = savedStateHandle[SEARCH_QUERY_KEY] ?: DEFAULT_QUERY

    private val searchQuery: StateFlow<String> =
        savedStateHandle.getStateFlow(SEARCH_QUERY_KEY, DEFAULT_QUERY)

    internal val isFavoriteOnly: StateFlow<Boolean> =
        savedStateHandle.getStateFlow(FAVORITE_ONLY_KEY, false)

    private val _advancedFilters = MutableStateFlow(AdvancedFilters())
    internal val advancedFilters: StateFlow<AdvancedFilters> = _advancedFilters.asStateFlow()

    // endregion

    // region Filtering pipeline

    /**
     * All inputs are merged into a single [BeersFilter]. It's the only thing the data layer sees:
     * any change of it that makes a difference restarts paging with the new request.
     * `distinctUntilChanged` makes sure that changes which don't affect the request
     * (e.g. applying the same filters again) don't trigger a reload.
     */
    @OptIn(FlowPreview::class)
    private val beersFilter: Flow<BeersFilter> = combine(
        searchQuery.debounce(SEARCH_DEBOUNCE).distinctUntilChanged(),
        isFavoriteOnly,
        advancedFilters
    ) { query, favoriteOnly, advanced ->
        advanced.applyTo(BeersFilter(query = query, favoritesOnly = favoriteOnly))
    }
        .distinctUntilChanged()

    /** The UI only renders what comes out of this flow. */
    @OptIn(ExperimentalCoroutinesApi::class)
    internal val beersFlow: Flow<PagingData<BeerItemUiModel>> = beersFilter
        .flatMapLatest { filter -> getBeersFlowUseCase(filter) }
        .map { pagingData -> pagingData.map { beer -> beer.toItemUiModel() } }
        .cachedIn(viewModelScope)

    // endregion

    internal val favoriteIds: StateFlow<Set<Int>> = getFavoriteIdsFlowUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    internal fun accept(query: String) {
        savedStateHandle[SEARCH_QUERY_KEY] = query
    }

    internal fun onFavoriteFilterToggle() {
        savedStateHandle[FAVORITE_ONLY_KEY] = !isFavoriteOnly.value
    }

    internal fun onFavoriteFilterReset() {
        savedStateHandle[FAVORITE_ONLY_KEY] = false
    }

    internal fun onAdvancedFiltersApply(filters: AdvancedFilters) {
        _advancedFilters.value = filters
    }

    internal fun onAdvancedFiltersReset() {
        _advancedFilters.value = AdvancedFilters()
    }

    internal fun onFavoriteClick(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            toggleFavoriteUseCase(id)
        }
    }

    companion object {
        private const val SEARCH_QUERY_KEY: String = "last_search_query"
        private const val FAVORITE_ONLY_KEY: String = "favorite_only"
        private const val DEFAULT_QUERY = ""
        private val SEARCH_DEBOUNCE = 300.milliseconds
    }
}
