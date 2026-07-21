package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * A chip's fixed footprint. Labels ellipsize rather than widen it, so callers
 * laying chips out in a row can work out how many fit by arithmetic alone.
 */
val CategoryChipWidth: Dp = 56.dp

/**
 * A category as a disc with a label beneath. Selected chips are the solid group
 * color ringed against the sheet surface; unselected are the color at 16% with a
 * colored glyph.
 */
@Composable
fun CategoryChip(
    label: String,
    icon: ImageVector,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ringColor: Color = CashizardTheme.colors.surfaceSheet,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .width(CategoryChipWidth)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.space4),
    ) {
        IconDisc(
            icon = icon,
            color = color,
            style = if (selected) IconDiscStyle.Solid else IconDiscStyle.Tinted,
            size = dimens.iconDiscChip,
            iconSize = 20.dp,
            // The gap ring is drawn as a border on a slightly larger footprint:
            // 2dp of surface, then 1.5dp of the group color.
            modifier = if (selected) {
                Modifier
                    .border(1.5.dp, color, CircleShape)
                    .padding(1.5.dp)
                    .border(2.dp, ringColor, CircleShape)
                    .padding(2.dp)
            } else {
                Modifier
            },
        )
        Text(
            text = label,
            style = CashizardTheme.typography.tabLabel.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = if (selected) colors.textPrimary else colors.textTertiary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
