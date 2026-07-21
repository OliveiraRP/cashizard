package com.houseofrafa.cashizard.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * The large screen title shared by the three tabs, with the space pill on the
 * right and an optional extra control before it.
 */
@Composable
fun ScreenHeader(
    title: String,
    spaceName: String?,
    modifier: Modifier = Modifier,
    onSpaceClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = dimens.screenPadding, end = dimens.screenPadding, bottom = dimens.space4),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = title,
            style = CashizardTheme.typography.largeTitle,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            modifier = Modifier.padding(bottom = 5.dp),
        ) {
            if (trailing != null) trailing()
            if (spaceName != null) SpacePill(spaceName, onSpaceClick)
        }
    }
}

/** The active space, shown as an avatar-and-name pill. */
@Composable
private fun SpacePill(spaceName: String, onClick: (() -> Unit)?) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Row(
        modifier = Modifier
            .background(colors.surfaceChip, RoundedCornerShape(dimens.radiusPill))
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
            .padding(start = dimens.space4, end = 10.dp, top = dimens.space4, bottom = dimens.space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(24.dp).background(colors.accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = spaceName.take(1).uppercase(),
                style = CashizardTheme.typography.caption2,
                color = colors.onAccent,
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = spaceName,
            style = CashizardTheme.typography.footnoteStrong,
            color = colors.textSecondary,
            maxLines = 1,
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = Lucide.ChevronDown,
            contentDescription = null,
            tint = colors.textQuaternary,
            modifier = Modifier.size(15.dp),
        )
    }
}
