package com.houseofrafa.core.ui.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.houseofrafa.core.ui.theme.CashizardTheme

@Composable
fun MainHeader(
    title: String,
    modifier: Modifier = Modifier,
    leftButton: @Composable (() -> Unit)? = null,
    rightButton: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CashizardTheme.spacing.marginTopLarge),
        ) {
            if (leftButton != null) {
                Box(Modifier.align(Alignment.CenterStart)) { leftButton() }
            }
            if (rightButton != null) {
                Box(Modifier.align(Alignment.CenterEnd)) { rightButton() }
            }
        }
        Text(
            text     = title,
            style    = CashizardTheme.typography.displaySmall,
            color    = CashizardTheme.colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(CashizardTheme.spacing.xl))
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailingText: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = CashizardTheme.spacing.xs),
    ) {
        if (icon != null) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = CashizardTheme.colors.textSecondary,
                modifier           = Modifier.size(CashizardTheme.spacing.iconSize),
            )
            Spacer(Modifier.width(CashizardTheme.spacing.xs))
        }

        Text(
            text     = title,
            style    = CashizardTheme.typography.headlineSmall,
            color    = CashizardTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )

        when {
            trailingContent != null -> trailingContent()
            trailingText != null    -> Text(
                text  = trailingText,
                style = CashizardTheme.typography.titleMedium,
                color = CashizardTheme.colors.textSecondary,
            )
        }
    }
}
