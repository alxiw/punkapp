package io.github.alxiw.punkapp.presentation.beers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.alxiw.punkapp.presentation.R
import io.github.alxiw.punkapp.presentation.common.DialogSystemBarIcons
import io.github.alxiw.punkapp.presentation.common.toCleanString
import java.time.YearMonth
import java.util.Locale

private const val MAX_DECIMAL_LENGTH = 8
private const val MONTH_YEAR_LENGTH = 6
private const val MIN_BREWED_YEAR = 1900

/**
 * Full-screen dialog to edit [AdvancedFilters]. The fields are edited in a local state:
 * [onDismissRequest] closes the dialog without saving it, [onApply] gets the validated result.
 */
@Composable
internal fun AdvancedFiltersDialog(
    filters: AdvancedFilters,
    onDismissRequest: () -> Unit,
    onApply: (AdvancedFilters) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        DialogSystemBarIcons()
        AdvancedFiltersScreen(
            filters = filters,
            onBackClick = onDismissRequest,
            onApply = onApply
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedFiltersScreen(
    filters: AdvancedFilters,
    onBackClick: () -> Unit,
    onApply: (AdvancedFilters) -> Unit,
) {
    var abvGt by rememberSaveable { mutableStateOf(filters.abvGt.toFieldText()) }
    var abvLt by rememberSaveable { mutableStateOf(filters.abvLt.toFieldText()) }
    var ibuGt by rememberSaveable { mutableStateOf(filters.ibuGt.toFieldText()) }
    var ibuLt by rememberSaveable { mutableStateOf(filters.ibuLt.toFieldText()) }
    var ebcGt by rememberSaveable { mutableStateOf(filters.ebcGt.toFieldText()) }
    var ebcLt by rememberSaveable { mutableStateOf(filters.ebcLt.toFieldText()) }
    var brewedAfter by rememberSaveable { mutableStateOf(filters.brewedAfter.toFieldText()) }
    var brewedBefore by rememberSaveable { mutableStateOf(filters.brewedBefore.toFieldText()) }
    var food by rememberSaveable { mutableStateOf(filters.food.orEmpty()) }

    val focusManager = LocalFocusManager.current
    val maxBrewedYear = YearMonth.now().year

    val isAbvRangeError = isNumberRangeError(abvGt, abvLt)
    val isIbuRangeError = isNumberRangeError(ibuGt, ibuLt)
    val isEbcRangeError = isNumberRangeError(ebcGt, ebcLt)
    val afterDate = parseMonthYear(brewedAfter, maxBrewedYear)
    val beforeDate = parseMonthYear(brewedBefore, maxBrewedYear)
    val isBrewedRangeError = afterDate != null && beforeDate != null && afterDate.isAfter(beforeDate)

    val hasErrors = isAbvRangeError || isIbuRangeError || isEbcRangeError || isBrewedRangeError ||
        listOf(abvGt, abvLt, ibuGt, ibuLt, ebcGt, ebcLt).any { it.isNotEmpty() && it.toDoubleOrNull() == null } ||
        (brewedAfter.isNotEmpty() && afterDate == null) ||
        (brewedBefore.isNotEmpty() && beforeDate == null)
    val hasValues = listOf(abvGt, abvLt, ibuGt, ibuLt, ebcGt, ebcLt, brewedAfter, brewedBefore, food)
        .any { it.isNotEmpty() }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            // Shrinks the whole screen above the keyboard: the bottom bar stays visible and the
            // scrollable content keeps the focused field out of the keyboard area.
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        FilterRoundButton(
                            onClick = onBackClick,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        FilterRoundButton(
                            onClick = {
                                abvGt = ""
                                abvLt = ""
                                ibuGt = ""
                                ibuLt = ""
                                ebcGt = ""
                                ebcLt = ""
                                brewedAfter = ""
                                brewedBefore = ""
                                food = ""
                                focusManager.clearFocus()
                            },
                            enabled = hasValues,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = stringResource(R.string.reset_filters)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        // The content scrolls under the button: the bar fades from fully
                        // transparent at the top to the screen background color at the bottom.
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                            )
                        )
                        .navigationBarsPadding()
                        .padding(
                            start = 16.dp,
                            top = 24.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                ) {
                    Button(
                        onClick = {
                            onApply(
                                AdvancedFilters(
                                    abvGt = abvGt.toDoubleOrNull(),
                                    abvLt = abvLt.toDoubleOrNull(),
                                    ibuGt = ibuGt.toDoubleOrNull(),
                                    ibuLt = ibuLt.toDoubleOrNull(),
                                    ebcGt = ebcGt.toDoubleOrNull(),
                                    ebcLt = ebcLt.toDoubleOrNull(),
                                    brewedAfter = afterDate,
                                    brewedBefore = beforeDate,
                                    food = food.trim().ifEmpty { null },
                                )
                            )
                        },
                        enabled = !hasErrors,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.apply),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    // The top and the bottom bars are transparent, so the content scrolls under
                    // them and their heights are reserved inside the scrollable content.
                    .padding(
                        start = 16.dp,
                        top = paddingValues.calculateTopPadding(),
                        end = 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 16.dp
                    )
            ) {
                NumberRangeSection(
                    title = stringResource(R.string.header_strength),
                    greaterThan = abvGt,
                    onGreaterThanChange = { abvGt = sanitizeDecimal(it) },
                    lessThan = abvLt,
                    onLessThanChange = { abvLt = sanitizeDecimal(it) },
                    isRangeError = isAbvRangeError
                )
                NumberRangeSection(
                    title = stringResource(R.string.header_bitterness),
                    greaterThan = ibuGt,
                    onGreaterThanChange = { ibuGt = sanitizeDecimal(it) },
                    lessThan = ibuLt,
                    onLessThanChange = { ibuLt = sanitizeDecimal(it) },
                    isRangeError = isIbuRangeError
                )
                NumberRangeSection(
                    title = stringResource(R.string.header_color),
                    greaterThan = ebcGt,
                    onGreaterThanChange = { ebcGt = sanitizeDecimal(it) },
                    lessThan = ebcLt,
                    onLessThanChange = { ebcLt = sanitizeDecimal(it) },
                    isRangeError = isEbcRangeError
                )

                FilterSection(title = stringResource(R.string.first_brewed)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterTextField(
                            value = brewedAfter,
                            onValueChange = { raw ->
                                val digits = sanitizeMonthYear(raw)
                                val isJustCompleted = digits.length == MONTH_YEAR_LENGTH &&
                                    brewedAfter.length < MONTH_YEAR_LENGTH
                                brewedAfter = digits
                                if (isJustCompleted) focusManager.moveFocus(FocusDirection.Next)
                            },
                            placeholder = stringResource(R.string.brewed_from_hint),
                            isError = (brewedAfter.isNotEmpty() && afterDate == null) || isBrewedRangeError,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                            visualTransformation = MonthYearVisualTransformation,
                            modifier = Modifier.weight(1f)
                        )
                        FilterTextField(
                            value = brewedBefore,
                            onValueChange = { brewedBefore = sanitizeMonthYear(it) },
                            placeholder = stringResource(R.string.brewed_to_hint),
                            isError = (brewedBefore.isNotEmpty() && beforeDate == null) || isBrewedRangeError,
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                            visualTransformation = MonthYearVisualTransformation,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FilterSection(title = stringResource(R.string.header_food_pairing)) {
                    FilterTextField(
                        value = food,
                        onValueChange = { food = it },
                        placeholder = stringResource(R.string.food_hint),
                        isError = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/** Round button on a light gray background, the same as in the top bars of the other screens. */
@Composable
private fun FilterRoundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = CircleShape
        ),
        content = content
    )
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun NumberRangeSection(
    title: String,
    greaterThan: String,
    onGreaterThanChange: (String) -> Unit,
    lessThan: String,
    onLessThanChange: (String) -> Unit,
    isRangeError: Boolean,
) {
    FilterSection(title = title) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterTextField(
                value = greaterThan,
                onValueChange = onGreaterThanChange,
                placeholder = stringResource(R.string.more_than),
                isError = isRangeError || greaterThan.isInvalidNumber(),
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
                modifier = Modifier.weight(1f)
            )
            FilterTextField(
                value = lessThan,
                onValueChange = onLessThanChange,
                placeholder = stringResource(R.string.less_than),
                isError = isRangeError || lessThan.isInvalidNumber(),
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = { Text(text = placeholder, maxLines = 1) },
        isError = isError,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
    )
}

/**
 * Shows the digits of a month and a year as `MM.YYYY`: `042007` -> `04.2007`.
 * The dot is only a decoration, the underlying text contains the digits only.
 */
internal object MonthYearVisualTransformation : VisualTransformation {

    private const val DOT_POSITION = 2

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.take(MONTH_YEAR_LENGTH)
        val formatted = if (digits.length > DOT_POSITION) {
            digits.substring(0, DOT_POSITION) + "." + digits.substring(DOT_POSITION)
        } else {
            digits
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safe = offset.coerceIn(0, digits.length)
                return if (safe > DOT_POSITION) safe + 1 else safe
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safe = offset.coerceIn(0, formatted.length)
                return if (safe > DOT_POSITION) safe - 1 else safe
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

private fun Double?.toFieldText(): String = this?.toCleanString().orEmpty()

private fun YearMonth?.toFieldText(): String =
    this?.let { "%02d%04d".format(Locale.ROOT, it.monthValue, it.year) }.orEmpty()

/** Keeps digits and at most one dot; a comma (decimal separator of some keyboards) becomes a dot. */
private fun sanitizeDecimal(input: String): String {
    var hasDot = false
    return buildString {
        for (char in input) {
            when {
                char in '0'..'9' -> append(char)
                (char == '.' || char == ',') && !hasDot -> {
                    hasDot = true
                    append('.')
                }
            }
        }
    }.take(MAX_DECIMAL_LENGTH)
}

private fun sanitizeMonthYear(input: String): String =
    input.filter { it in '0'..'9' }.take(MONTH_YEAR_LENGTH)

/** A non-empty text that is not a number, e.g. a lonely dot. */
private fun String.isInvalidNumber(): Boolean = isNotEmpty() && toDoubleOrNull() == null

/** Both bounds are set and the lower one is not less than the upper one. */
private fun isNumberRangeError(greaterThan: String, lessThan: String): Boolean {
    val gt = greaterThan.toDoubleOrNull() ?: return false
    val lt = lessThan.toDoubleOrNull() ?: return false
    return gt >= lt
}

/**
 * Parses `MMYYYY` digits. Returns `null` if the text is incomplete, the month is not in `01..12`
 * or the year is not in `1900..maxYear`.
 */
private fun parseMonthYear(digits: String, maxYear: Int): YearMonth? {
    if (digits.length != MONTH_YEAR_LENGTH) return null
    val month = digits.substring(0, 2).toIntOrNull() ?: return null
    val year = digits.substring(2).toIntOrNull() ?: return null
    if (month !in 1..12 || year !in MIN_BREWED_YEAR..maxYear) return null
    return YearMonth.of(year, month)
}

@Preview(showBackground = true)
@Composable
private fun AdvancedFiltersScreenPreview() {
    MaterialTheme {
        AdvancedFiltersScreen(
            filters = AdvancedFilters(
                abvGt = 8.0,
                abvLt = 5.0,
                brewedAfter = YearMonth.of(2007, 4),
            ),
            onBackClick = {},
            onApply = {}
        )
    }
}
