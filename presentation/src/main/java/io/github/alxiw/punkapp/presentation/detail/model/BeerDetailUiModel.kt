package io.github.alxiw.punkapp.presentation.detail.model

import io.github.alxiw.punkapp.domain.model.Amount
import io.github.alxiw.punkapp.domain.model.Beer
import io.github.alxiw.punkapp.domain.model.Duration
import io.github.alxiw.punkapp.domain.model.FermentationTemp
import io.github.alxiw.punkapp.domain.model.Hop
import io.github.alxiw.punkapp.domain.model.Ingredients
import io.github.alxiw.punkapp.domain.model.Malt
import io.github.alxiw.punkapp.domain.model.MashTemp
import io.github.alxiw.punkapp.domain.model.Method
import io.github.alxiw.punkapp.domain.model.Temp
import io.github.alxiw.punkapp.domain.model.Volume
import io.github.alxiw.punkapp.presentation.common.DateFormatter
import io.github.alxiw.punkapp.presentation.common.ValueUnit
import kotlin.Int

internal data class BeerDetailUiModel(
    val id: Int,
    val name: String,
    val tagline: String,
    val firstBrewed: String,
    val imageUrl: String,
    val abv: Double,
    val ibu: Double?,
    val ebc: Double?,
    val srm: Double?,
    val description: String,
    val foodPairing: List<String>,
    val isFavorite: Boolean = false,
    val targetOg: Double?,
    val targetFg: Double?,
    val ph: Double?,
    val attenuationLevel: Double?,
    val volume: VolumeUiModel,
    val boilVolume: VolumeUiModel,
    val method: MethodUiModel,
    val ingredients: IngredientsUiModel,
    val brewersTips: String,
    val contributedBy: String,
)

internal data class VolumeUiModel(
    val value: Int,
    val unit: String
) {
    fun toVolumeText(): String {
        return value.toString() + ValueUnit.convertToDisplayName(unit)
    }
}

internal data class MethodUiModel(
    val mashTemp: List<MashTempUiModel>,
    val fermentation: FermentationTempUiModel,
    val twist: List<String>
)

internal data class TempUiModel(
    val value: Double?,
    val unit: String
)

internal data class DurationUiModel(
    val value: Int?,
    val unit: String
)

internal data class MashTempUiModel(
    val temp: TempUiModel,
    val duration: DurationUiModel
)

internal data class FermentationTempUiModel(
    val temp: TempUiModel
)

internal data class AmountUiModel(
    val value: Double,
    val unit: String
)

internal data class IngredientsUiModel(
    val malt: List<MaltUiModel>,
    val hops: List<HopUiModel>,
    val yeast: String?
)

internal data class MaltUiModel(
    val name: String,
    val amount: AmountUiModel
)

internal data class HopUiModel(
    val name: String,
    val amount: AmountUiModel,
    val add: String,
    val attribute: String?
)

const val EMPTY_PLACEHOLDER = "∅"

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
    ibu = ibu,
    ebc = ebc,
    srm = srm,
    targetOg = targetOg,
    targetFg = targetFg,
    ph = ph,
    attenuationLevel = attenuationLevel,
    volume = volume.toUiModel(),
    boilVolume = boilVolume.toUiModel(),
    method = method.toUiModel(),
    ingredients = ingredients.toUiModel(),
    foodPairing = foodPairing,
    brewersTips = brewersTips,
    contributedBy = contributedBy,
    isFavorite = isFavorite
)

private fun Volume.toUiModel() = VolumeUiModel(
    value = value,
    unit = unit
)

private fun Method.toUiModel() = MethodUiModel(
    mashTemp = mash.map { it.toUiModel() },
    fermentation = fermentation.toUiModel(),
    twist = twist
)

private fun MashTemp.toUiModel() = MashTempUiModel(
    temp = temp.toUiModel(),
    duration = duration.toUiModel()
)

private fun FermentationTemp.toUiModel() = FermentationTempUiModel(
    temp = temp.toUiModel()
)

private fun Temp.toUiModel() = TempUiModel(
    value = value,
    unit = unit
)

private fun Duration.toUiModel() = DurationUiModel(
    value = value,
    unit = unit
)

private fun Amount.toUiModel() = AmountUiModel(
    value = value,
    unit = unit
)

private fun Ingredients.toUiModel() = IngredientsUiModel(
    malt = malt.map { it.toUiModel() },
    hops = hops.map { it.toUiModel() },
    yeast = yeast
)

private fun Malt.toUiModel() = MaltUiModel(
    name = name,
    amount = amount.toUiModel()
)

private fun Hop.toUiModel() = HopUiModel(
    name = name,
    amount = amount.toUiModel(),
    add = add,
    attribute = attribute
)
