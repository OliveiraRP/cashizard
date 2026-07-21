package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/** Circular icon button treatments used in sheet headers and screen headers. */
enum class CircleButtonStyle {
    /** Translucent gray fill with a light glyph — close, back, overflow. */
    Neutral,

    /** Accent fill with a white glyph — the sheet's primary action. */
    Accent,
}

/**
 * A 36dp circular icon button. The design uses these instead of text actions in
 * sheet headers.
 */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    style: CircleButtonStyle = CircleButtonStyle.Neutral,
    enabled: Boolean = true,
    size: Dp = CashizardTheme.dimens.iconDisc,
    iconSize: Dp = 20.dp,
) {
    val colors = CashizardTheme.colors
    val background = when (style) {
        CircleButtonStyle.Neutral -> colors.fillControl
        CircleButtonStyle.Accent -> if (enabled) colors.accent else colors.accent.copy(alpha = 0.4f)
    }
    val tint = when (style) {
        CircleButtonStyle.Neutral -> colors.textSecondary
        CircleButtonStyle.Accent -> colors.onAccent
    }

    Box(
        modifier = modifier
            .size(size)
            .background(background, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize),
        )
    }
}
