package io.github.alxiw.punkapp.presentation.beers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.compose.LazyPagingItems
import io.github.alxiw.punkapp.presentation.R
import io.github.alxiw.punkapp.presentation.beers.model.BeerItemUiModel

private val CardPadding = 24.dp
private val DialogVerticalMargin = 48.dp
private val ScrollbarThickness = 3.dp
private val ScrollbarRightInset = 5.dp
private val ScrollbarMinLength = 24.dp

@Composable
internal fun BeerPreviewDialog(
    beer: BeerItemUiModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        BoxWithConstraints {
            BeerPreviewCard(
                beer = beer,
                modifier = Modifier.heightIn(max = maxHeight - DialogVerticalMargin)
            )
        }
    }
}

/**
 * Resolves the previewed beer from the currently presented paging items, so only the beer id
 * has to survive configuration changes.
 */
@Composable
internal fun BeerPreviewHost(
    beers: LazyPagingItems<BeerItemUiModel>,
    previewBeerId: Int?,
    onDismiss: () -> Unit
) {
    val beer = previewBeerId
        ?.let { id -> beers.itemSnapshotList.items.firstOrNull { it.id == id } }
        ?: return
    BeerPreviewDialog(beer = beer, onDismiss = onDismiss)
}

@Composable
internal fun BeerPreviewCard(
    beer: BeerItemUiModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .widthIn(max = 420.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(CardPadding)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = beer.name,
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = beer.tagline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                PreviewSection(stringResource(R.string.header_description)) {
                    Text(
                        text = beer.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                    )
                }

                if (beer.foodPairing.isNotEmpty()) {
                    PreviewSection(stringResource(R.string.header_food_pairing)) {
                        beer.foodPairing.forEach { food ->
                            Text(
                                text = "• $food",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                            )
                        }
                    }
                }
            }

            PreviewScrollbar(
                state = scrollState,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

@Composable
private fun PreviewScrollbar(
    state: ScrollState,
    modifier: Modifier = Modifier
) {
    val viewport = state.viewportSize
    val max = state.maxValue
    if (viewport <= 0 || max <= 0 || max == Int.MAX_VALUE) return

    val color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)

    Canvas(modifier = modifier) {
        val inset = CardPadding.toPx()
        val thickness = ScrollbarThickness.toPx()
        val trackHeight = size.height - inset * 2
        if (trackHeight <= 0f) return@Canvas

        val thumbHeight = (trackHeight * viewport / (viewport + max.toFloat()))
            .coerceIn(ScrollbarMinLength.toPx(), trackHeight)
        val fraction = (state.value.toFloat() / max).coerceIn(0f, 1f)

        drawRoundRect(
            color = color,
            topLeft = Offset(
                x = size.width - ScrollbarRightInset.toPx() - thickness,
                y = inset + (trackHeight - thumbHeight) * fraction
            ),
            size = Size(thickness, thumbHeight),
            cornerRadius = CornerRadius(thickness / 2f)
        )
    }
}

@Composable
internal fun PreviewSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun BeerPreviewCardPreview() {
    MaterialTheme {
        BeerPreviewCard(
            beer = BeerItemUiModel(
                id = 366,
                name = "Lost Lager",
                tagline = "Dry-Hopped Pilsner.",
                imageUrl = "https://punkapi.dev/api/v3/images/366.png",
                abv = 4.5,
                ibu = 37.0,
                ebc = 5.0,
                firstBrewed = "DECEMBER 2018",
                description = "A pilsner that combines the light, crisp and clean lager profile provided by Weihenstephan's house yeast, with the vibrant citrus and stonefruit aromas associated with new German hop Saphir. This lager is easy-going but has subtle depths; toast, hints of spice and a zesty lime marmalade character.",
                foodPairing = listOf("Spicy thai curry", "Grilled steak", "Cheesecake"),
                isFavorite = true
            )
        )
    }
}
