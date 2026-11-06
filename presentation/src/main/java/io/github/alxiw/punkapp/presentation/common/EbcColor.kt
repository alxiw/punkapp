package io.github.alxiw.punkapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.max

private const val MIN_HUE = 18f
private const val MAX_HUE = 48f
private const val MIN_LIGHTNESS = 18f
private const val MAX_LIGHTNESS = 62f
private const val SATURATION = 0.8f

internal val EbcMarkerSize = 10.dp
internal val EbcMarkerSpacing = 2.dp

/**
 * Color marker for the EBC scale; null if the value is unknown.
 * Higher values are darker and less saturated, the same way as the web client does.
 */
internal fun ebcColorOrNull(ebc: Double?): Color? {
    val value = ebc?.toFloat() ?: return null

    return Color.hsl(
        hue = max(MIN_HUE, MAX_HUE - value / 5f),
        saturation = SATURATION,
        lightness = max(MIN_LIGHTNESS, MAX_LIGHTNESS - value / 4f) / 100f
    )
}

@Composable
internal fun Modifier.ebcMarkerModifier(color: Color?): Modifier {
    if (color == null) return Modifier

    return this
        .background(color = color, shape = CircleShape)
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = CircleShape
        )
}
