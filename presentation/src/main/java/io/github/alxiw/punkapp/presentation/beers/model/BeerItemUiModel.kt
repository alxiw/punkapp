package io.github.alxiw.punkapp.presentation.beers.model

import androidx.compose.runtime.Immutable
import io.github.alxiw.punkapp.domain.model.Beer
import io.github.alxiw.punkapp.presentation.common.DateFormatter

@Immutable
internal data class BeerItemUiModel(
    val id: Int,
    val name: String,
    val tagline: String,
    val imageUrl: String,
    val abv: String,
    val ibu: String?,
    val ebc: String?,
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
    ibu = ibu.toStatValueOrNull(),
    ebc = ebc.toStatValueOrNull(),
    firstBrewed = DateFormatter.formatDate(firstBrewed, short = true),
    description = description,
    foodPairing = foodPairing,
    isFavorite = isFavorite
)

/**
 * Null for empty values, so the list does not render anything for unknown IBU/EBC.
 */
internal fun String.toStatValueOrNull(): String? {
    return takeIf { it.isNotBlank() }
}
