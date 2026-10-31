package io.github.alxiw.punkapp.presentation.beers.model

import androidx.compose.runtime.Immutable
import io.github.alxiw.punkapp.domain.model.Beer

@Immutable
internal data class BeerItemUiModel(
    val id: Int,
    val name: String,
    val tagline: String,
    val imageUrl: String,
    val abv: String,
    val ibu: String,
    val ebc: String,
    val firstBrewed: String,
    val description: String,
    val foodPairing: List<String>,
    val isFavorite: Boolean = false
)

internal fun Beer.toItemUiModel() = BeerItemUiModel(
    id = id,
    name = name,
    tagline = tagline,
    imageUrl = imageUrl,
    abv = abv,
    ibu = ibu,
    ebc = ebc,
    firstBrewed = firstBrewed,
    description = description,
    foodPairing = foodPairing,
    isFavorite = isFavorite
)
