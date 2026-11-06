package io.github.alxiw.punkapp.presentation.common

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

internal class EbcColorTest {

    @Test
    @DisplayName("Returns null when the value is absent")
    fun returnsNullForAbsentValue() {
        assertNull(ebcColorOrNull(null))
    }

    @Test
    @DisplayName("Maps the EBC value to the web color")
    fun mapsEbcValueToColor() {
        val testCases = listOf(
            5.0 to Color.hsl(47f, 0.8f, 0.6075f),
            10.0 to Color.hsl(46f, 0.8f, 0.595f),
            50.0 to Color.hsl(38f, 0.8f, 0.495f)
        )

        for ((ebc, expectedColor) in testCases) {
            val actualColor = ebcColorOrNull(ebc)
            assertNotNull(actualColor, "Color should not be null for EBC $ebc")
            assertEquals(expectedColor, actualColor, "Color mismatch for EBC $ebc")
        }
    }

    @Test
    @DisplayName("Clamps the color for the darkest values")
    fun clampsDarkestValues() {
        val expectedColor = Color.hsl(18f, 0.8f, 0.18f)
        val testCases = listOf(300.0, 1000.0)

        for (ebc in testCases) {
            val actualColor = ebcColorOrNull(ebc)
            assertNotNull(actualColor, "Color should not be null for EBC $ebc")
            assertEquals(expectedColor, actualColor, "Color mismatch for EBC $ebc")
        }
    }
}
