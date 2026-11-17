package io.github.alxiw.punkapp.presentation.beers

import io.github.alxiw.punkapp.domain.model.BeersFilter

/**
 * State of the "Filters" button: [isActive] switches the parameters below on and off,
 * so the user can keep the values and quickly toggle them. There is no UI to edit
 * the parameters yet, so all of them are `null` for now.
 */
internal data class AdvancedFilters(
    val isActive: Boolean = false,
    val abvGt: Double? = null,
    val abvLt: Double? = null,
    val ibuGt: Double? = null,
    val ibuLt: Double? = null,
    val ebcGt: Double? = null,
    val ebcLt: Double? = null,
    val food: String? = null,
) {
    /** Applies the parameters to [filter] if the filters are switched on. */
    fun applyTo(filter: BeersFilter): BeersFilter = if (isActive) {
        filter.copy(
            abvGt = abvGt,
            abvLt = abvLt,
            ibuGt = ibuGt,
            ibuLt = ibuLt,
            ebcGt = ebcGt,
            ebcLt = ebcLt,
            food = food,
        )
    } else {
        filter
    }
}
