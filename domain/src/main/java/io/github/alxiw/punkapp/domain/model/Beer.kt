package io.github.alxiw.punkapp.domain.model

data class Beer(
    val id: Int,
    val name: String,
    val tagline: String,
    val firstBrewed: String,
    val description: String,
    val imageUrl: String,
    val abv: Double,
    val ibu: Double?,
    val ebc: Double?,
    val srm: Double?,
    val foodPairing: List<String>,
    val isFavorite: Boolean = false,
    val targetOg: Double?,
    val targetFg: Double?,
    val ph: Double?,
    val attenuationLevel: Double?,
    val volume: Volume,
    val boilVolume: Volume,
    val method: Method,
    val ingredients: Ingredients,
    val brewersTips: String,
    val contributedBy: String,
)

data class Volume(
    val value: Int,
    val unit: String,
)

data class Method(
    val mash: List<MashTemp>,
    val fermentation: FermentationTemp,
    val twist: List<String>
)

data class Temp(
    val value: Double?,
    val unit: String,
)

data class Duration(
    val value: Int?,
    val unit: String
)

data class MashTemp(
    val temp: Temp,
    val duration: Duration
)

data class FermentationTemp(
    val temp: Temp
)

data class Amount(
    val value: Double,
    val unit: String,
)

data class Ingredients(
    val malt: List<Malt>,
    val hops: List<Hop>,
    val yeast: String?
)

data class Malt(
    val name: String,
    val amount: Amount
)

data class Hop(
    val name: String,
    val amount: Amount,
    val add: String,
    val attribute: String?
)
