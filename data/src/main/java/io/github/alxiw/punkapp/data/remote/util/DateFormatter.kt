package io.github.alxiw.punkapp.data.remote.util

import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Locale

internal object DateFormatter {

    private val MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy")
    private val YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy")

    fun formatDate(string: String, short: Boolean): String {
        return try {
            YearMonth.parse(string, MONTH_YEAR_FORMATTER).run {
                val style = if (short) TextStyle.SHORT else TextStyle.FULL
                "${month.getDisplayName(style, Locale.US).uppercase(Locale.US)} $year"
            }
        } catch (e: DateTimeParseException) {
            try {
                Year.parse(string, YEAR_FORMATTER).run {
                    toString()
                }
            } catch (e: DateTimeParseException) {
                string
            }
        }
    }
}
