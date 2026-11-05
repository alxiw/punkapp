package io.github.alxiw.punkapp.presentation.common

internal enum class ValueUnit(val fullName: String, val displayName: String) {
    LITERS("litres", "L"),
    GRAMS("grams", "g"),
    KILOGRAMS("kilograms", "kg"),
    CELSIUS("celsius", "°C"),
    MINUTES("minutes", "min");

    companion object {
        fun convertToDisplayName(fullName: String): String {
            return entries.find { it.fullName == fullName }?.displayName ?: fullName
        }
    }
}
