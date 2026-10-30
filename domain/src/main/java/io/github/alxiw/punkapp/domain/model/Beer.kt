package io.github.alxiw.punkapp.domain.model

data class Beer(
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
    val method: Method,
    val ingredients: Ingredients,
    val foodPairing: List<String>,
    val brewersTips: String,
    val contributedBy: String,
    val isFavorite: Boolean = false
)

data class Method(
    val mashTemp: List<MashTemp>,
    val fermentation: Fermentation,
    val twist: List<String>
)

data class MashTemp(
    val temp: String,
    val duration: String?
)

data class Fermentation(
    val temp: String
)

data class Ingredients(
    val malt: List<Malt>,
    val hops: List<Hop>,
    val yeast: String?
)

data class Malt(
    val name: String,
    val amount: String
)

data class Hop(
    val name: String,
    val amount: String,
    val add: String,
    val attribute: String?
)
