package io.github.alxiw.punkapp.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import io.github.alxiw.punkapp.presentation.R
import io.github.alxiw.punkapp.presentation.detail.model.BeerDetailUiModel
import io.github.alxiw.punkapp.presentation.detail.model.BeerDetailUiState
import io.github.alxiw.punkapp.presentation.detail.model.FermentationUiModel
import io.github.alxiw.punkapp.presentation.detail.model.HopUiModel
import io.github.alxiw.punkapp.presentation.detail.model.IngredientsUiModel
import io.github.alxiw.punkapp.presentation.detail.model.MaltUiModel
import io.github.alxiw.punkapp.presentation.detail.model.MashTempUiModel
import io.github.alxiw.punkapp.presentation.detail.model.MethodUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeerDetailScreen(
    viewModel: BeerDetailViewModel = hiltViewModel(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

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
                actions = {
                    FavoriteAction(
                        isFavorite = (uiState as? BeerDetailUiState.Success)?.model?.isFavorite,
                        onFavoriteClick = viewModel::onFavoriteClick
                    )
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
            when (val state = uiState) {
                is BeerDetailUiState.Loading -> {
                    BeersDetailLoading(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is BeerDetailUiState.Success -> {
                    BeerDetailContent(
                        beer = state.model,
                        animatedVisibilityScope = animatedVisibilityScope,
                        sharedTransitionScope = sharedTransitionScope,
                        paddingValues = paddingValues
                    )
                }
                is BeerDetailUiState.Error -> {
                    BeerDetailError(
                        message = state.exception.message,
                        onRetry = { viewModel.retry() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun BeersDetailLoading(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(modifier = modifier)
}

@Composable
private fun BeerDetailError(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message ?: "Unknown error",
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
private fun FavoriteAction(
    isFavorite: Boolean?,
    onFavoriteClick: () -> Unit
) {
    isFavorite ?: return
    IconButton(
        onClick = onFavoriteClick,
        modifier = Modifier
            .padding(end = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = CircleShape
            )
    ) {
        Icon(
            painter = if (isFavorite) {
                painterResource(id = R.drawable.ic_heart_filled)
            } else {
                painterResource(id = R.drawable.ic_heart_outline)
            },
            contentDescription = "Favorite",
            tint = if (isFavorite) colorResource(R.color.favorite) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun BeerDetailContent(
    beer: BeerDetailUiModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    paddingValues: PaddingValues
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        BeerHeroSection(
            beer = beer,
            animatedVisibilityScope = animatedVisibilityScope,
            sharedTransitionScope =  sharedTransitionScope,
            paddingValues = paddingValues
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // title
            Text(
                text = beer.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .beerSharedElement(
                        key = "name-${beer.id}",
                        scope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )

            // tagline
            Text(
                text = beer.tagline,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .beerSharedElement(
                        key = "tagline-${beer.id}",
                        scope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )

            // description
            DetailSection(stringResource(R.string.header_description)) {
                Text(
                    text = beer.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
            }

            // basics
            DetailSection(stringResource(R.string.header_basics)) {
                LabelValueRow(stringResource(R.string.volume), beer.volume)
                LabelValueRow(stringResource(R.string.boil_volume), beer.boilVolume)
                LabelValueRow(stringResource(R.string.abv), beer.abv)
                LabelValueRow(stringResource(R.string.target_fg), beer.targetFg)
                LabelValueRow(stringResource(R.string.target_og), beer.targetOg)
                LabelValueRow(stringResource(R.string.ebc), beer.ebc)
                LabelValueRow(stringResource(R.string.srm), beer.srm)
                LabelValueRow(stringResource(R.string.ph), beer.ph)
                LabelValueRow(stringResource(R.string.attenuation_level), beer.attenuationLevel)
            }

            BeerMethodSection(beer.method)

            BeerIngredientsSection(beer.ingredients)

            // food pairing
            DetailSection(stringResource(R.string.header_food_pairing)) {
                beer.foodPairing.forEach { food ->
                    Text(
                        text = food,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                    )
                }
            }

            // tips
            DetailSection(stringResource(R.string.header_brewers_tips)) {
                Text(
                    text = beer.brewersTips,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
            }

            // contributed by
            DetailSection(stringResource(R.string.header_contributed_by)) {
                Text(
                    text = beer.contributedBy,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(paddingValues.calculateBottomPadding())
        )
    }
}

@Composable
private fun Modifier.beerSharedElement(
    key: String,
    scope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
): Modifier = with(scope) {
    sharedElement(
        sharedContentState = rememberSharedContentState(key = key),
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@Composable
private fun BeerHeroSection(
    beer: BeerDetailUiModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = paddingValues.calculateTopPadding()),
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
                    .size(width = 160.dp, height = 360.dp)
                    .padding(top = 16.dp)
                    .beerSharedElement(
                        key = "image-${beer.id}",
                        scope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp, start = 4.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .beerSharedElement(
                            key = "date-${beer.id}",
                            scope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                ) {
                    Text(
                        text = stringResource(R.string.first_brewed),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = beer.firstBrewed,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                        .beerSharedElement(
                            key = "abv-${beer.id}",
                            scope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                ) {
                    StatItem(
                        title = stringResource(R.string.abv),
                        value = beer.abv
                    )
                    StatItem(
                        title = stringResource(R.string.ibu),
                        value = beer.ibu
                    )
                    StatItem(
                        title = stringResource(R.string.og),
                        value = beer.targetOg
                    )
                }
            }
        }
    }
}

@Composable
private fun BeerMethodSection(method: MethodUiModel) {
    DetailSection(stringResource(R.string.header_method)) {
        method.mashTemp.forEachIndexed { index, mash ->
            val text = if (mash.duration.isNullOrBlank()) {
                mash.temp
            } else {
                "${mash.temp} (${mash.duration})"
            }
            val label = if (index == 0) stringResource(R.string.mash) else ""
            LabelValueRow(label, text, 0.5f, 1.5f)
        }
        method.fermentation.temp.let {
            LabelValueRow(stringResource(R.string.fermentation), it)
        }
        if (method.twist.isNotEmpty()) {
            method.twist.forEachIndexed { index, twist ->
                val label = if (index == 0) stringResource(R.string.twist) else ""
                LabelValueRow(label, twist, 0.5f, 2f)
            }
        }
    }
}

@Composable
private fun BeerIngredientsSection(ingredients: IngredientsUiModel) {
    val isMaltNotEmpty = ingredients.malt.isNotEmpty()
    val isHopsNotEmpty = ingredients.hops.isNotEmpty()
    val isYeastNotEmpty = !ingredients.yeast.isNullOrBlank()

    if (isMaltNotEmpty || isHopsNotEmpty || isYeastNotEmpty) {
        DetailSection(stringResource(R.string.header_ingredients)) {
            if (isMaltNotEmpty) {
                IngredientSubSection(stringResource(R.string.malt)) {
                    ingredients.malt.forEach { malt ->
                        LabelValueRow(malt.name, malt.amount, 1f, 0.7f)
                    }
                }
            }

            if (isHopsNotEmpty) {
                IngredientSubSection(stringResource(R.string.hops)) {
                    ingredients.hops.forEach { hop ->
                        val label = if (hop.attribute.isNullOrEmpty()) {
                            "${hop.name} (${hop.add})"
                        } else {
                            "${hop.name} (${hop.add}, ${hop.attribute})"
                        }
                        LabelValueRow(label, hop.amount, 1.5f, 0.5f)
                    }
                }
            }

            if (isYeastNotEmpty) {
                IngredientSubSection(stringResource(R.string.yeast)) {
                    LabelValueRow(ingredients.yeast, null)
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
private fun IngredientSubSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 8.dp)
    )
    content()
}

@Composable
private fun LabelValueRow(
    label: String,
    value: String?,
    labelWeight: Float = 1f,
    valueWeight: Float = 1f
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(labelWeight)
        )
        value?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(valueWeight)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BeerDetailErrorPreview() {
    MaterialTheme {
        BeerDetailError(
            message = "Something went wrong",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BeerDetailContentPreview() {
    val sampleBeer = BeerDetailUiModel(
        id = 1,
        name = "Lost Lager",
        tagline = "Dry-Hopped Pilsner.",
        firstBrewed = "DECEMBER 2018",
        description = "A pilsner that combines the light, crisp and clean lager profile provided by Weihenstephan's house yeast, with the vibrant citrus and stonefruit aromas associated with new German hop Saphir. This lager is easy-going but has subtle depths; toast, hints of spice and a zesty lime marmalade character.",
        imageUrl = "https://punkapi.dev/api/v3/images/366.png",
        abv = "4.5",
        ibu = "37",
        targetOg = "1056",
        targetFg = "1010",
        ebc = "5",
        srm = "8.5",
        ph = "4.4",
        attenuationLevel = "82.1",
        volume = "20L",
        boilVolume = "25L",
        method = MethodUiModel(
            mashTemp = listOf(MashTempUiModel("65°C", "75")),
            fermentation = FermentationUiModel("19°C"),
            twist = emptyList()
        ),
        ingredients = IngredientsUiModel(
            malt = listOf(MaltUiModel("Extra Pale", "5.3kg")),
            hops = listOf(HopUiModel("Citra", "12.5g", "start", "bitter")),
            yeast = "Wyeast 1056"
        ),
        foodPairing = listOf("Spicy thai curry", "Grilled steak", "Cheesecake"),
        brewersTips = "While the boil is vigorous, ensure you keep a close eye on the temperature.",
        contributedBy = "Alexander Ivanovsky <alxiw>"
    )

    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            MaterialTheme {
                BeerDetailContent(
                    beer = sampleBeer,
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    paddingValues = PaddingValues.Zero
                )
            }
        }
    }
}
