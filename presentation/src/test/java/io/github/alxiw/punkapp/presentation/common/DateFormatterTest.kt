package io.github.alxiw.punkapp.presentation.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

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
    fun `formats date according to short flag`(input: String, short: Boolean, expected: String) {
        assertEquals(expected, DateFormatter.formatDate(input, short))
    }

    @DisplayName("Returns empty string when input is empty")
    @Test
    fun `returns empty string when input is empty`() {
        assertEquals("", DateFormatter.formatDate("", short = false))
    }

    @DisplayName("Returns original string unchanged when input is not a date")
    @ParameterizedTest(name = "short = {0}")
    @ValueSource(booleans = [false, true])
    fun `returns original string when input is not a date`(short: Boolean) {
        val randomString = UUID.randomUUID().toString()
        assertEquals(randomString, DateFormatter.formatDate(randomString, short))
    }
}
