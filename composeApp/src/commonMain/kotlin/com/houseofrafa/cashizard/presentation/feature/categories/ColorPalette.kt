package com.houseofrafa.cashizard.presentation.feature.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors

private const val COLOR_COLUMNS = 4
private val SWATCH = 36.dp
private val SWATCH_RING = SWATCH + 8.dp

/**
 * The group palette as a card of swatches, four to a row. The selected swatch
 * gets the same gap ring the category chips use.
 */
@Composable
internal fun ColorPalette(
    colors: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimens.radiusControl))
            .background(theme.surfaceVariant)
            .padding(dimens.space16),
        verticalArrangement = Arrangement.spacedBy(dimens.space16),
    ) {
        colors.chunked(COLOR_COLUMNS).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { hex ->
                    ColorSwatch(
                        hex = hex,
                        selected = hex.equals(selected, ignoreCase = true),
                        onClick = { onSelect(hex) },
                    )
                }
                // Keeps a short final row aligned with the ones above it.
                repeat(COLOR_COLUMNS - row.size) { Spacer(Modifier.size(SWATCH_RING)) }
            }
        }
    }
}

@Composable
private fun ColorSwatch(hex: String, selected: Boolean, onClick: () -> Unit) {
    val color = CategoryColors.parse(hex)
    val ringColor = CashizardTheme.colors.surfaceVariant

    Box(
        modifier = Modifier
            .size(SWATCH_RING)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = if (selected) {
                // The design's ring: 2dp of card surface, then 2dp of the color.
                Modifier
                    .size(SWATCH_RING)
                    .border(2.dp, color, CircleShape)
                    .padding(2.dp)
                    .border(2.dp, ringColor, CircleShape)
                    .padding(2.dp)
            } else {
                Modifier.size(SWATCH)
            }.background(color, CircleShape),
        )
    }
}
