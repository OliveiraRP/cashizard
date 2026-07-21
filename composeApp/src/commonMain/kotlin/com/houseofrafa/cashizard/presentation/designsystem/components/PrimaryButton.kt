package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * Filled accent call-to-action. Shows a spinner and blocks input while [loading];
 * dims and blocks input while disabled.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val interactive = enabled && !loading
    val shape = RoundedCornerShape(dimens.radiusControl)
    val bg = if (interactive) colors.accent else colors.accent.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.buttonHeight)
            .clip(shape)
            .background(bg, shape)
            .clickable(enabled = interactive, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = colors.onAccent,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp),
            )
        } else {
            Text(
                text = text,
                style = CashizardTheme.typography.headline,
                color = colors.onAccent,
            )
        }
    }
}
