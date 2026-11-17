package io.github.alxiw.punkapp.domain.model

import java.time.YearMonth

/**
 * All parameters the beers list can be filtered by.
 * `null` / blank values mean "no restriction" and are not sent to the API at all.
 *
 * Favorites are intentionally a flag instead of a list of ids: the ids snapshot is taken
 * by the repository when paging starts, so toggling a heart doesn't restart the Pager.
 */
data class BeersFilter(
    val query: String = "",
    val abvGt: Double? = null,
    val abvLt: Double? = null,
    val ibuGt: Double? = null,
    val ibuLt: Double? = null,
    val ebcGt: Double? = null,
    val ebcLt: Double? = null,
    val brewedAfter: YearMonth? = null,
    val brewedBefore: YearMonth? = null,
    val food: String? = null,
    val favoritesOnly: Boolean = false,
)
