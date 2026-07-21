package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/** The three disc treatments the design uses. */
enum class IconDiscStyle {
    /** Solid category color with a white glyph — transaction & analytics rows. */
    Solid,

    /** Category color at 16% with a full-color glyph — unselected category chips. */
    Tinted,

    /** Neutral gray disc with a light glyph — wallet rows, where color is meaningless. */
    Neutral,
}

/**
 * An icon inside a circle. The [style] decides the treatment; [size] follows the
 * row it sits in (see the `iconDisc*` dimens), and the glyph is half the disc.
 */
@Composable
fun IconDisc(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = CashizardTheme.colors.accent,
    style: IconDiscStyle = IconDiscStyle.Solid,
    size: Dp = CashizardTheme.dimens.iconDisc,
    iconSize: Dp = size / 2,
    contentDescription: String? = null,
) {
    val colors = CashizardTheme.colors
    val background = when (style) {
        IconDiscStyle.Solid -> color
        IconDiscStyle.Tinted -> color.copy(alpha = 0.16f)
        IconDiscStyle.Neutral -> colors.surfaceHigh
    }
    val tint = when (style) {
        IconDiscStyle.Solid -> colors.onAccent
        IconDiscStyle.Tinted -> color
        IconDiscStyle.Neutral -> colors.textSecondary
    }

    Box(
        modifier = modifier.size(size).background(background, CircleShape),
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
