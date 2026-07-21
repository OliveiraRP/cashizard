package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/** One row of a [PopoverMenu]. */
data class PopoverMenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

/**
 * An iOS context menu: a narrow translucent card of labelled rows, divided by
 * full-width hairlines. The caller positions it; it does not manage its own
 * anchoring or scrim.
 */
@Composable
fun PopoverMenu(
    items: List<PopoverMenuItem>,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val shape = RoundedCornerShape(13.dp)

    Column(
        modifier = modifier
            .width(224.dp)
            .shadow(elevation = 20.dp, shape = shape)
            .clip(shape)
            .background(colors.popover, shape)
            .border(0.5.dp, colors.popoverBorder, shape),
    ) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = dimens.rowHeightMin)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = item.onClick,
                    )
                    .padding(horizontal = dimens.space16, vertical = dimens.space12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.label,
                    style = CashizardTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (index != items.lastIndex) {
                Box(
                    Modifier.fillMaxWidth().height(0.5.dp).background(colors.popoverSeparator),
                )
            }
        }
    }
}
