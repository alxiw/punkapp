package io.github.alxiw.punkapp.presentation.detail.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

private const val EMPTY_PLACEHOLDER = "∅"

internal class PlaceholderTest {

    @DisplayName("Keeps known values")
    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @CsvSource("37, 37", "DECEMBER 2018, DECEMBER 2018")
    fun keepsKnownValues(input: String, expected: String) {
        assertEquals(expected, input.orPlaceholder())
    }

    @DisplayName("Replaces empty values with the placeholder")
    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @ValueSource(strings = ["", " "])
    fun replacesEmptyValues(input: String) {
        assertEquals(EMPTY_PLACEHOLDER, input.orPlaceholder())
    }

    @Test
    @DisplayName("Replaces an absent value with the placeholder")
    fun replacesNull() {
        val input: String? = null

        assertEquals(EMPTY_PLACEHOLDER, input.orPlaceholder())
    }
}
