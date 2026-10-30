package io.github.alxiw.punks.data.remote.util

import io.github.alxiw.punks.data.remote.model.AmountResponse
import io.github.alxiw.punks.data.remote.model.TempResponse
import io.github.alxiw.punks.data.remote.model.VolumeResponse
import kotlin.text.isNotBlank

internal fun VolumeResponse.toVolumeText(): String {
    return value.toString() + ValueUnit.convertToDisplayName(unit)
}

internal fun TempResponse.toTempText(): String {
    return value?.let { it.toString() + ValueUnit.convertToDisplayName(unit) } ?: EMPTY_PLACEHOLDER
}

internal fun AmountResponse.toAmountText(): String {
    return value.toCleanString() + ValueUnit.convertToDisplayName(unit)
}

internal fun Float?.toStringOrDefault(): String {
    return this?.toCleanString() ?: EMPTY_PLACEHOLDER
}

internal fun Int?.toDurationInMinutes(): String? {
    return this?.let { "$it min" }
}

internal fun Int?.toStringOrDefault(): String {
    return this?.toString() ?: EMPTY_PLACEHOLDER
}

internal fun String?.toStringOrDefault(): String {
    return this?.takeIf { it.isNotBlank() } ?: EMPTY_PLACEHOLDER
}

internal fun String?.toDateOrDefault(): String {
    return this?.let { DateFormatter.formatDate(it, false) } ?: EMPTY_PLACEHOLDER
}

internal fun Float.toCleanString(): String {
    return if (this % 1.0 == 0.0) toLong().toString() else toString()
}

private const val EMPTY_PLACEHOLDER = "∅"
