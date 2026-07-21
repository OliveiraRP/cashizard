package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * A settings-style row inside an inset card: leading glyph, label, optional
 * trailing value and chevron, or an arbitrary trailing slot (e.g. a switch).
 */
@Composable
fun InsetRow(
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: String? = null,
    showChevron: Boolean = false,
    onClick: (() -> Unit)? = null,
    minHeight: Dp = CashizardTheme.dimens.rowHeightCompact,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .defaultMinSize(minHeight = minHeight)
            .padding(horizontal = dimens.space16, vertical = dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(dimens.space12))

        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                style = CashizardTheme.typography.bodyLarge,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = CashizardTheme.typography.caption,
                    color = colors.textQuaternary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (value != null) {
            Spacer(Modifier.width(dimens.space8))
            Text(
                text = value,
                style = CashizardTheme.typography.bodyLarge,
                color = colors.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
