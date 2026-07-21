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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * The floating action button that opens the add-transaction sheet. Sits above
 * the tab bar with an accent-tinted drop shadow.
 */
@Composable
fun Fab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Lucide.Plus,
    contentDescription: String = "Add transaction",
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Box(
        modifier = modifier
            .size(dimens.fabSize)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = colors.accent,
                spotColor = colors.accent,
            )
            .background(colors.accent, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.onAccent,
            modifier = Modifier.size(28.dp),
        )
    }
}
