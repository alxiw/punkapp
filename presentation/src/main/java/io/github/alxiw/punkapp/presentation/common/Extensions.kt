package io.github.alxiw.punkapp.presentation.common

internal fun Double.toCleanString(): String {
    return if (this % 1.0 == 0.0) toLong().toString() else toString()
}

/** Null for empty values, so the list does not render anything for unknown IBU/EBC */
internal fun String.toStatValueOrNull(): String? {
    return takeIf { it.isNotBlank() }
}
