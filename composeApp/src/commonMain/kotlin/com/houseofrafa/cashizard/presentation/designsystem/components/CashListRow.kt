package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * The standard grouped-list row: optional leading slot (usually an [IconDisc]),
 * a title with optional subtitle, and an optional trailing slot.
 *
 * The design uses two shapes of this row — transaction rows (36dp disc, medium
 * title, 13pt subtitle) and wallet rows (30dp disc, regular title, 12pt
 * subtitle) — so the title style and paddings are parameters rather than fixed.
 */
@Composable
fun CashListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleStyle: TextStyle = CashizardTheme.typography.rowTitle,
    subtitleStyle: TextStyle = CashizardTheme.typography.rowSubtitle,
    /** Rendered under the title instead of [subtitle] — e.g. a goal progress bar. */
    subtitleSlot: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    showChevron: Boolean = false,
    onClick: (() -> Unit)? = null,
    minHeight: Dp = 0.dp,
    verticalPadding: Dp = 10.dp,
) {
    val dimens = CashizardTheme.dimens
    val colors = CashizardTheme.colors
    val rowModifier = modifier
        .fillMaxWidth()
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        .defaultMinSize(minHeight = minHeight)
        .padding(horizontal = dimens.space16, vertical = verticalPadding)

    Row(verticalAlignment = Alignment.CenterVertically, modifier = rowModifier) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(dimens.space12))
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = titleStyle,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            when {
                subtitleSlot != null -> subtitleSlot()
                subtitle != null -> Text(
                    text = subtitle,
                    style = subtitleStyle,
                    color = colors.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(dimens.space12))
            trailing()
        }
        if (showChevron) {
            Spacer(Modifier.width(dimens.space4))
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = colors.textPlaceholder,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
