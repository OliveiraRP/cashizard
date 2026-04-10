package com.houseofrafa.core.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.houseofrafa.core.ui.theme.CashizardTheme

@Composable
fun LoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.height(52.dp),
        shape = CashizardTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor         = CashizardTheme.colors.brandPrimary,
            contentColor           = CashizardTheme.colors.textOnBrand,
            disabledContainerColor = CashizardTheme.colors.brandPrimarySubtle,
            disabledContentColor   = CashizardTheme.colors.textTertiary,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier    = Modifier.size(20.dp),
                color       = CashizardTheme.colors.textOnBrand,
                strokeWidth = 2.dp,
            )
        } else {
            Text(text, style = CashizardTheme.typography.labelLarge)
        }
    }
}

@Composable
fun HeaderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
) {
    val shape        = CashizardTheme.shapes.full
    val contentColor = CashizardTheme.colors.textPrimary
    val glassFill    = contentColor.copy(alpha = 0.15f)
    val glassBorder  = contentColor.copy(alpha = 0.18f)
    val iconSize     = CashizardTheme.spacing.headerButtonIconSize
    val buttonHeight = CashizardTheme.spacing.headerButtonHeight

    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .height(buttonHeight)
            .clip(shape)
            .background(glassFill, shape)
            .border(0.5.dp, glassBorder, shape)
            .clickable(onClick = onClick)
            .padding(
                horizontal = if (label != null) CashizardTheme.spacing.sm else CashizardTheme.spacing.xs,
            ),
    ) {
        if (icon != null) {
            Icon(
                imageVector        = icon,
                contentDescription = label,
                tint               = contentColor,
                modifier           = Modifier.size(iconSize),
            )
        }
        if (label != null) {
            Text(
                text  = label,
                style = CashizardTheme.typography.bodyMedium,
                color = contentColor,
            )
        }
    }
}
