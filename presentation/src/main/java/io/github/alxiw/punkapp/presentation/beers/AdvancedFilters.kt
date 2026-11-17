package io.github.alxiw.punkapp.presentation.beers

import io.github.alxiw.punkapp.domain.model.BeersFilter
import java.time.YearMonth

/**
 * Parameters edited in the "Filters" dialog. `null` means "no restriction" for a parameter.
 * The filters are considered [isActive] as long as at least one of them is set.
 */
internal data class AdvancedFilters(
    val abvGt: Double? = null,
    val abvLt: Double? = null,
    val ibuGt: Double? = null,
    val ibuLt: Double? = null,
    val ebcGt: Double? = null,
    val ebcLt: Double? = null,
    val brewedAfter: YearMonth? = null,
    val brewedBefore: YearMonth? = null,
    val food: String? = null,
) {
    /** `true` if at least one parameter is set. */
    val isActive: Boolean
        get() = abvGt != null || abvLt != null ||
            ibuGt != null || ibuLt != null ||
            ebcGt != null || ebcLt != null ||
            brewedAfter != null || brewedBefore != null ||
            !food.isNullOrBlank()

    /** Applies the parameters to [filter]. */
    fun applyTo(filter: BeersFilter): BeersFilter = filter.copy(
        abvGt = abvGt,
        abvLt = abvLt,
        ibuGt = ibuGt,
        ibuLt = ibuLt,
        ebcGt = ebcGt,
        ebcLt = ebcLt,
        brewedAfter = brewedAfter,
        brewedBefore = brewedBefore,
        food = food?.trim()?.takeIf { it.isNotEmpty() },
    )
}
