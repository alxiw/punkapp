package io.github.alxiw.punkapp.data.remote.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

private const val EMPTY_PLACEHOLDER = "∅"

internal class DateFormatterTest {

    @DisplayName("Formats the first brewed value")
    @ParameterizedTest(name = "\"{0}\" with short = {1} -> \"{2}\"")
    @CsvSource(
        "12/2018, false, DECEMBER 2018",
        "12/2018, true, DEC 2018",
        "01/2007, false, JANUARY 2007",
        "01/2007, true, JAN 2007",
        "2018, false, 2018",
        "2018, true, 2018",
        "very old, false, very old",
        "13/2018, false, 13/2018",
        "1/2018, false, 1/2018"
    )
    fun formatsDate(input: String, short: Boolean, expected: String) {
        assertEquals(expected, DateFormatter.formatDate(input, short))
    }

    @DisplayName("Keeps the empty placeholder")
    @ParameterizedTest(name = "short = {0}")
    @CsvSource("false", "true")
    fun keepsEmptyPlaceholder(short: Boolean) {
        assertEquals(EMPTY_PLACEHOLDER, DateFormatter.formatDate(EMPTY_PLACEHOLDER, short))
    }
}
