package io.github.alxiw.punkapp.presentation.beers

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import io.github.alxiw.punkapp.presentation.R
import io.github.alxiw.punkapp.presentation.beers.model.BeerItemUiModel
import io.github.alxiw.punkapp.presentation.common.toCleanString

private val AbvColumnWidth = 50.dp
private val IbuColumnWidth = 58.dp
private val EbcColumnWidth = 58.dp
private val StatIconSize = 16.dp
private val StatMarkerSize = 10.dp
private val StatMarkerSpacing = 2.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeersScreen(
    viewModel: BeersViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onBeerClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
) {
    val beers = viewModel.beersFlow.collectAsLazyPagingItems()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    var isSearchActive by rememberSaveable { mutableStateOf(viewModel.initialQuery.isNotEmpty()) }
    var searchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(viewModel.initialQuery))
    }
    val focusRequester = remember { FocusRequester() }
    var shouldRequestFocus by remember { mutableStateOf(false) }
    var previewBeerId by rememberSaveable { mutableStateOf<Int?>(null) }

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        searchQuery = TextFieldValue("")
        viewModel.accept("")
    }

    LaunchedEffect(searchQuery) {
        viewModel.accept(searchQuery.text)
    }

    LaunchedEffect(shouldRequestFocus) {
        if (shouldRequestFocus) {
            focusRequester.requestFocus()
            shouldRequestFocus = false
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = onFavoritesClick,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(imageVector = Icons.Outlined.Favorite, contentDescription = "Favorites")
                        }
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = !isSearchActive,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                onClick = {
                                    isSearchActive = true
                                    shouldRequestFocus = true
                                },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )

                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
                    exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BeersSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onCloseClick = {
                            isSearchActive = false
                            searchQuery = TextFieldValue("")
                            viewModel.accept("")
                        },
                        focusRequester = focusRequester
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // use beers.loadState.mediator?.refresh with higher priority when use remote mediator
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
                    BeersList(
                        beers = beers,
                        favoriteIds = favoriteIds,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        contentPadding = paddingValues,
                        onBeerClick = onBeerClick,
                        onBeerLongClick = { previewBeerId = it },
                        onFavoriteClick = viewModel::onFavoriteClick
                    )
                }
            }

            BeerPreviewHost(
                beers = beers,
                previewBeerId = previewBeerId,
                onDismiss = { previewBeerId = null }
            )
        }
    }
}

@Composable
internal fun BeersSearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onCloseClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape,
        modifier = modifier
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(vertical = 8.dp)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (query.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }
            )
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
internal fun BeersLoading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
internal fun BeersEmpty(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.empty),
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
internal fun BeersError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(
                text = stringResource(R.string.retry),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun BeersList(
    beers: LazyPagingItems<BeerItemUiModel>,
    favoriteIds: Set<Int>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    contentPadding: PaddingValues,
    onBeerClick: (Int) -> Unit,
    onBeerLongClick: (Int) -> Unit,
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
                BeerItem(
                    beer = beer,
                    isFavorite = isFavorite,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope,
                    onFavoriteClick = { onFavoriteClick(beer.id) },
                    onClick = { onBeerClick(beer.id) },
                    onLongClick = { onBeerLongClick(beer.id) }
                )
                HorizontalDivider()
            }
        }

        when (val appendState = beers.loadState.append) {
            is LoadState.Loading -> item { LoadingItem() }
            is LoadState.Error -> item { ErrorItem(appendState.error) { beers.retry() } }
            else -> Unit
        }
    }
}

@Composable
internal fun BeerItem(
    beer: BeerItemUiModel,
    isFavorite: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val ebcColor = remember(beer.ebc) { ebcColorOrNull(beer.ebc) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .combinedClickable(
                onLongClickLabel = stringResource(R.string.preview),
                onLongClick = onLongClick,
                onClick = onClick
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(beer.imageUrl)
                .placeholder(R.drawable.placeholder_bottle)
                .crossfade(true)
                .precision(Precision.EXACT)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(width = 50.dp, height = 75.dp)
                .sharedElementIfNotPreview(
                    sharedTransitionScope = sharedTransitionScope,
                    key = "image-${beer.id}",
                    animatedVisibilityScope = animatedVisibilityScope
                ),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = beer.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .sharedElementIfNotPreview(
                        sharedTransitionScope = sharedTransitionScope,
                        key = "name-${beer.id}",
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
            )
            Text(
                text = beer.tagline,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .sharedElementIfNotPreview(
                        sharedTransitionScope = sharedTransitionScope,
                        key = "tagline-${beer.id}",
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.sharedElementIfNotPreview(
                        sharedTransitionScope = sharedTransitionScope,
                        key = "stats-${beer.id}",
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${beer.abv.toCleanString()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(AbvColumnWidth)
                    )

                    Row(
                        modifier = Modifier.width(IbuColumnWidth),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        beer.ibu?.let { ibu ->
                            Icon(
                                painter = painterResource(R.drawable.ic_beer_ibu),
                                contentDescription = stringResource(R.string.ibu),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(StatIconSize)
                            )
                            Text(
                                text = ibu.toCleanString(),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.width(EbcColumnWidth),
                        horizontalArrangement = Arrangement.spacedBy(StatMarkerSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        beer.ebc?.let { ebc ->
                            Box(
                                modifier = Modifier
                                    .size(StatMarkerSize)
                                    .then(ebcMarkerModifier(ebcColor))
                            )
                            Text(
                                text = ebc.toCleanString(),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Text(
                    text = beer.firstBrewed,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .sharedElementIfNotPreview(
                            sharedTransitionScope = sharedTransitionScope,
                            key = "date-${beer.id}",
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .padding(start = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = if (isFavorite) painterResource(id = R.drawable.ic_heart_filled) else painterResource(id = R.drawable.ic_heart_outline),
                    contentDescription = null,
                    tint = if (isFavorite) colorResource(R.color.favorite) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "#${beer.id}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ebcMarkerModifier(color: Color?): Modifier {
    if (color == null) return Modifier

    return Modifier
        .background(color = color, shape = CircleShape)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = CircleShape
        )
}

@Composable
internal fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun ErrorItem(
    error: Throwable,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = error.localizedMessage ?: "Unknown error",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Button(
                onClick = onClick,
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
internal fun Modifier.sharedElementIfNotPreview(
    sharedTransitionScope: SharedTransitionScope,
    key: Any,
    animatedVisibilityScope: AnimatedVisibilityScope
): Modifier {
    val isPreview = LocalInspectionMode.current
    return if (isPreview) this
    else with(sharedTransitionScope) {
        this@sharedElementIfNotPreview.sharedElement(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BeersLoadingPreview() {
    MaterialTheme {
        BeersLoading()
    }
}

@Preview(showBackground = true)
@Composable
private fun BeersEmptyPreview() {
    MaterialTheme {
        BeersEmpty()
    }
}

@Preview(showBackground = true)
@Composable
private fun BeersErrorPreview() {
    MaterialTheme {
        BeersError(
            message = "Something went wrong",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingItemPreview() {
    MaterialTheme {
        LoadingItem()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorItemPreview() {
    MaterialTheme {
        ErrorItem(
            error = Exception("Something went wrong"),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BeerItemPreview() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            MaterialTheme {
                BeerItem(
                    beer = BeerItemUiModel(
                        id = 366,
                        name = "Lost Lager",
                        tagline = "Dry-Hopped Pilsner.",
                        imageUrl = "https://punkapi.dev/api/v3/images/001.png",
                        abv = 10.96,
                        ibu = 1157.0,
                        ebc = 800.0,
                        firstBrewed = "DEC 2018",
                        description = "A pilsner that combines the light, crisp and clean lager profile provided by Weihenstephan's house yeast, with the vibrant citrus and stonefruit aromas associated with new German hop Saphir.",
                        foodPairing = listOf("Spicy thai curry", "Grilled steak", "Cheesecake"),
                        isFavorite = true
                    ),
                    isFavorite = true,
                    animatedVisibilityScope = this@AnimatedVisibility,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onFavoriteClick = {},
                    onClick = {},
                    onLongClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BeersSearchBarPreview() {
    MaterialTheme {
        BeersSearchBar(
            query = TextFieldValue("My name is"),
            onQueryChange = {},
            onCloseClick = {},
            focusRequester = remember { FocusRequester() }
        )
    }
}
