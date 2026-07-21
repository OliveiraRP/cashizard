package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * A thin rounded progress bar used by goal wallets and analytics rows.
 * [progress] is clamped to 0f..1f.
 */
@Composable
fun MicroProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = CashizardTheme.colors.accent,
    trackColor: Color = CashizardTheme.colors.fillTrack,
    height: Dp = CashizardTheme.dimens.progressBarHeight,
) {
    val clamped = progress.coerceIn(0f, 1f)
    val shape = RoundedCornerShape(CashizardTheme.dimens.progressBarRadius)
    // Width is the caller's to decide: 90dp beside a goal wallet, full width in
    // an analytics row.
    Box(
        modifier = modifier
            .height(height)
            .background(trackColor, shape),
    ) {
        if (clamped > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(clamped)
                    .fillMaxHeight()
                    .background(color, shape),
            )
        }
    }
}
