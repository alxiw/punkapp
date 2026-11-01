package io.github.alxiw.punkapp.data.remote.model

import io.github.alxiw.punkapp.data.remote.api.BeersSearchService
import io.github.alxiw.punkapp.data.remote.util.toAmountText
import io.github.alxiw.punkapp.data.remote.util.toDurationInMinutes
import io.github.alxiw.punkapp.data.remote.util.toStringOrDefault
import io.github.alxiw.punkapp.data.remote.util.toTempText
import io.github.alxiw.punkapp.data.remote.util.toVolumeText
import io.github.alxiw.punkapp.domain.model.Beer
import io.github.alxiw.punkapp.domain.model.Fermentation
import io.github.alxiw.punkapp.domain.model.Hop
import io.github.alxiw.punkapp.domain.model.Ingredients
import io.github.alxiw.punkapp.domain.model.Malt
import io.github.alxiw.punkapp.domain.model.MashTemp
import io.github.alxiw.punkapp.domain.model.Method
import com.squareup.moshi.Json

internal data class BeerResponse(
    val id: Int,
    val name: String,
    val tagline: String,
    @Json(name = "first_brewed") val firstBrewed: String,
    val description: String,
    val image: String,
    val abv: Float,
    val ibu: Float?,
    @Json(name = "target_fg") val targetFg: Float?,
    @Json(name = "target_og") val targetOg: Float?,
    val ebc: Float?,
    val srm: Float?,
    val ph: Float?,
    @Json(name = "attenuation_level") val attenuationLevel: Float?,
    val volume: VolumeResponse,
    @Json(name = "boil_volume") val boilVolume: VolumeResponse,
    val method: MethodResponse,
    val ingredients: IngredientsResponse,
    @Json(name = "food_pairing") val foodPairing: List<String>,
    @Json(name = "brewers_tips") val brewersTips: String,
    @Json(name = "contributed_by") val contributedBy: String,
)

// volume

internal data class VolumeResponse(
    val value: Int,
    val unit: String // litres
)

// method

internal data class MethodResponse(
    @Json(name = "mash_temp") val mashTemp: List<MashTempResponse>, // not empty
    val fermentation: FermentationResponse,
    val twist: String?
)

internal data class MashTempResponse(
    val temp: TempResponse,
    val duration: Int?
)

internal data class FermentationResponse(
    val temp: TempResponse
)

internal data class TempResponse(
    val value: Int?,
    val unit: String // "celsius"
)

// ingredients

internal data class IngredientsResponse(
    val malt: List<MaltResponse>, // might be empty (1 case)
    val hops: List<HopResponse>, // might be empty (1 case)
    val yeast: String? // might be null (1 case)
)

internal data class MaltResponse(
    val name: String,
    val amount: AmountResponse
)

internal data class HopResponse(
    val name: String,
    val amount: AmountResponse,
    val add: String, // "start"/"middle"/"end", or also "Dry Hop", "60", "FWH" etc.
    val attribute: String?
)

internal data class AmountResponse(
    val value: Float,
    val unit: String // "kilograms" or "grams"
)

internal fun BeerResponse.toBeer() = Beer(
    id = id,
    name = name,
    tagline = tagline,
    firstBrewed = firstBrewed.toStringOrDefault(),
    description = description,
    imageUrl = "${BeersSearchService.ENDPOINT}images/$image",
    abv = "${abv.toStringOrDefault()}%",
    ibu = ibu.toStringOrDefault(),
    targetOg = targetOg.toStringOrDefault(),
    targetFg = targetFg.toStringOrDefault(),
    ebc = ebc.toStringOrDefault(),
    srm = srm.toStringOrDefault(),
    ph = ph.toStringOrDefault(),
    attenuationLevel = attenuationLevel.toStringOrDefault(),
    volume = volume.toVolumeText(),
    boilVolume = boilVolume.toVolumeText(),
    method = method.toDomain(),
    ingredients = ingredients.toDomain(),
    foodPairing = foodPairing,
    brewersTips = brewersTips,
    contributedBy = contributedBy
)

private fun MethodResponse.toDomain() = Method(
    mashTemp = mashTemp.map { it.toDomain() },
    fermentation = fermentation.toDomain(),
    twist = twist?.split(",")?.map { it.trim() } ?: emptyList()
)

private fun MashTempResponse.toDomain() = MashTemp(
    temp = temp.toTempText(),
    duration = duration?.toDurationInMinutes()
)

private fun FermentationResponse.toDomain() = Fermentation(
    temp = temp.toTempText()
)

private fun IngredientsResponse.toDomain() = Ingredients(
    malt = malt.map { it.toDomain() },
    hops = hops.map { it.toDomain() },
    yeast = yeast
)

private fun MaltResponse.toDomain() = Malt(
    name = name,
    amount = amount.toAmountText()
)

private fun HopResponse.toDomain() = Hop(
    name = name,
    amount = amount.toAmountText(),
    add = add,
    attribute = attribute
)
