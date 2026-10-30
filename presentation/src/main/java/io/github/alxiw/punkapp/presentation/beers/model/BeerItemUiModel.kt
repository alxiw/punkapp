package io.github.alxiw.punkapp.presentation.beers.model

import io.github.alxiw.punkapp.domain.model.Beer

internal data class BeerItemUiModel(
    val id: Int,
    val name: String,
    val tagline: String,
    val firstBrewed: String,
    val imageUrl: String,
    val abv: String,
    val isFavorite: Boolean = false
)

internal fun Beer.toItemUiModel() = BeerItemUiModel(
    id = id,
    name = name,
    tagline = tagline,
    imageUrl = imageUrl,
    firstBrewed = firstBrewed,
    abv = abv,
    isFavorite = isFavorite
)
