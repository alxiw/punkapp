package io.github.alxiw.punkapp.presentation.beers.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

internal class StatValueTest {

    @DisplayName("Keeps known values")
    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @CsvSource("37, 37", "5, 5", "0, 0", "1157, 1157")
    fun keepsKnownValues(input: String, expected: String) {
        assertEquals(expected, input.toStatValueOrNull())
    }

    @DisplayName("Hides empty values")
    @ParameterizedTest(name = "\"{0}\" -> null")
    @ValueSource(strings = ["", " "])
    fun hidesMissingValues(input: String) {
        assertNull(input.toStatValueOrNull())
    }
}
