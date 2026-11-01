package io.github.alxiw.punkapp.presentation.detail.model

import io.github.alxiw.punkapp.domain.model.Beer
import io.github.alxiw.punkapp.domain.model.Fermentation
import io.github.alxiw.punkapp.domain.model.Hop
import io.github.alxiw.punkapp.domain.model.Ingredients
import io.github.alxiw.punkapp.domain.model.Malt
import io.github.alxiw.punkapp.domain.model.MashTemp
import io.github.alxiw.punkapp.domain.model.Method
import io.github.alxiw.punkapp.presentation.common.DateFormatter

internal data class BeerDetailUiModel(
    val id: Int,
    val name: String,
    val tagline: String,
    val firstBrewed: String,
    val description: String,
    val imageUrl: String,
    val abv: String,
    val ibu: String,
    val targetOg: String,
    val targetFg: String,
    val ebc: String,
    val srm: String,
    val ph: String,
    val attenuationLevel: String,
    val volume: String,
    val boilVolume: String,
    val method: MethodUiModel,
    val ingredients: IngredientsUiModel,
    val foodPairing: List<String>,
    val brewersTips: String,
    val contributedBy: String,
    val isFavorite: Boolean = false
)

internal data class MethodUiModel(
    val mashTemp: List<MashTempUiModel>,
    val fermentation: FermentationUiModel,
    val twist: List<String>
)

internal data class MashTempUiModel(
    val temp: String,
    val duration: String?
)

internal data class FermentationUiModel(
    val temp: String
)

internal data class IngredientsUiModel(
    val malt: List<MaltUiModel>,
    val hops: List<HopUiModel>,
    val yeast: String?
)

internal data class MaltUiModel(
    val name: String,
    val amount: String
)

internal data class HopUiModel(
    val name: String,
    val amount: String,
    val add: String,
    val attribute: String?
)

private const val EMPTY_PLACEHOLDER = "∅"

/** The detail screen always shows the placeholder instead of an absent value */
internal fun String?.orPlaceholder(): String {
    return takeUnless { it.isNullOrBlank() } ?: EMPTY_PLACEHOLDER
}

internal fun Beer.toDetailUiModel() = BeerDetailUiModel(
    id = id,
    name = name,
    tagline = tagline,
    firstBrewed = DateFormatter.formatDate(firstBrewed, short = false).orPlaceholder(),
    description = description,
    imageUrl = imageUrl,
    abv = abv,
    ibu = ibu.orPlaceholder(),
    targetOg = targetOg.orPlaceholder(),
    targetFg = targetFg.orPlaceholder(),
    ebc = ebc.orPlaceholder(),
    srm = srm.orPlaceholder(),
    ph = ph.orPlaceholder(),
    attenuationLevel = attenuationLevel.orPlaceholder(),
    volume = volume,
    boilVolume = boilVolume,
    method = method.toUiModel(),
    ingredients = ingredients.toUiModel(),
    foodPairing = foodPairing,
    brewersTips = brewersTips,
    contributedBy = contributedBy,
    isFavorite = isFavorite
)

private fun Method.toUiModel() = MethodUiModel(
    mashTemp = mashTemp.map { it.toUiModel() },
    fermentation = fermentation.toUiModel(),
    twist = twist
)

private fun MashTemp.toUiModel() = MashTempUiModel(
    temp = temp.orPlaceholder(),
    duration = duration
)

private fun Fermentation.toUiModel() = FermentationUiModel(
    temp = temp.orPlaceholder()
)

private fun Ingredients.toUiModel() = IngredientsUiModel(
    malt = malt.map { it.toUiModel() },
    hops = hops.map { it.toUiModel() },
    yeast = yeast
)

private fun Malt.toUiModel() = MaltUiModel(
    name = name,
    amount = amount
)

private fun Hop.toUiModel() = HopUiModel(
    name = name,
    amount = amount,
    add = add,
    attribute = attribute
)
