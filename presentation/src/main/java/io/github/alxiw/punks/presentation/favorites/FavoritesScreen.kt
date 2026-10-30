package io.github.alxiw.punks.presentation.favorites

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import io.github.alxiw.punks.presentation.beers.BeerItem
import io.github.alxiw.punks.presentation.beers.BeersEmpty
import io.github.alxiw.punks.presentation.beers.BeersError
import io.github.alxiw.punks.presentation.beers.BeersLoading
import io.github.alxiw.punks.presentation.beers.ErrorItem
import io.github.alxiw.punks.presentation.beers.LoadingItem
import io.github.alxiw.punks.presentation.beers.model.BeerItemUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onBeerClick: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    val beers = viewModel.favoritesFlow.collectAsLazyPagingItems()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val refreshState = beers.loadState.refresh) {
                is LoadState.Loading -> BeersLoading(modifier = Modifier.align(Alignment.Center))
                is LoadState.Error -> BeersError(
                    message = refreshState.error.localizedMessage ?: "Unknown error",
                    onRetry = { beers.retry() },
                    modifier = Modifier.align(Alignment.Center)
                )
                is LoadState.NotLoading -> if (beers.itemCount == 0) {
                    BeersEmpty(modifier = Modifier.align(Alignment.Center))
                } else {
                    FavoritesList(
                        beers = beers,
                        favoriteIds = favoriteIds,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        contentPadding = paddingValues,
                        onBeerClick = onBeerClick,
                        onFavoriteClick = viewModel::onFavoriteClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesList(
    beers: LazyPagingItems<BeerItemUiModel>,
    favoriteIds: Set<Int>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    contentPadding: PaddingValues,
    onBeerClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        item { HorizontalDivider() }
        items(
            count = beers.itemCount,
            key = beers.itemKey { it.id },
            contentType = beers.itemContentType { "beer" }
        ) { index ->
            beers[index]?.let { beer ->
                val isFavorite = favoriteIds.contains(beer.id)
                // In Favorites screen, we only want to show it if it's still in favoriteIds
                // But PagingSource fetched it because it WAS a favorite.
                // If we unfavorite it, we can either let it stay until refresh or hide it.
                if (isFavorite) {
                    BeerItem(
                        beer = beer,
                        isFavorite = true,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        onFavoriteClick = { onFavoriteClick(beer.id) },
                        onClick = { onBeerClick(beer.id) }
                    )
                    HorizontalDivider()
                }
            }
        }

        when (val appendState = beers.loadState.append) {
            is LoadState.Loading -> item { LoadingItem() }
            is LoadState.Error -> item { ErrorItem(appendState.error) { beers.retry() } }
            else -> Unit
        }
    }
}
