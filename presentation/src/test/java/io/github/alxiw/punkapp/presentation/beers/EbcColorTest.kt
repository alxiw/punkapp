package io.github.alxiw.punkapp.presentation.beers

import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class EbcColorTest {

    @DisplayName("Returns null for unknown values")
    @ParameterizedTest(name = "\"{0}\" -> null")
    @ValueSource(strings = ["", " ", "∅", "–", "unknown"])
    fun returnsNullForUnknownValues(ebc: String) {
        assertNull(ebcColorOrNull(ebc))
    }

    @Test
    @DisplayName("Returns null when the value is absent")
    fun returnsNullForAbsentValue() {
        assertNull(ebcColorOrNull(null))
    }

    @Test
    @DisplayName("Maps the EBC value to the web color")
    fun mapsEbcValueToColor() {
        assertEquals(Color.hsl(47f, 0.8f, 0.6075f), ebcColorOrNull("5"))
        assertEquals(Color.hsl(46f, 0.8f, 0.595f), ebcColorOrNull("10"))
        assertEquals(Color.hsl(38f, 0.8f, 0.495f), ebcColorOrNull("50"))
    }

    @Test
    @DisplayName("Clamps the color for the darkest values")
    fun clampsDarkestValues() {
        assertEquals(Color.hsl(18f, 0.8f, 0.18f), ebcColorOrNull("300"))
        assertEquals(Color.hsl(18f, 0.8f, 0.18f), ebcColorOrNull("1000"))
    }
}
