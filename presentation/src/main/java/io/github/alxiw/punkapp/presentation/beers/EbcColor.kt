package io.github.alxiw.punkapp.presentation.beers

import androidx.compose.ui.graphics.Color
import kotlin.math.max

private const val MIN_HUE = 18f
private const val MAX_HUE = 48f
private const val MIN_LIGHTNESS = 18f
private const val MAX_LIGHTNESS = 62f
private const val SATURATION = 0.8f

/**
 * Color marker for the EBC scale; null if the value is unknown.
 * Higher values are darker and less saturated, the same way as the web client does.
 */
internal fun ebcColorOrNull(ebc: String?): Color? {
    val value = ebc?.toFloatOrNull() ?: return null

    return Color.hsl(
        hue = max(MIN_HUE, MAX_HUE - value / 5f),
        saturation = SATURATION,
        lightness = max(MIN_LIGHTNESS, MAX_LIGHTNESS - value / 4f) / 100f
    )
}
