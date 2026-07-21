package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * iOS grouped form container: a rounded card whose rows are divided by inset
 * hairlines. Separators are drawn between rows only — never above the first or
 * below the last — matching the design's inset-grouped list style.
 */
@Composable
fun FormCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CashizardTheme.dimens.radiusCard,
    separatorInset: Dp = CashizardTheme.dimens.formSeparatorInset,
    rows: List<@Composable () -> Unit>,
) {
    val colors = CashizardTheme.colors
    val shape = RoundedCornerShape(cornerRadius)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surface, shape),
    ) {
        rows.forEachIndexed { index, row ->
            row()
            if (index != rows.lastIndex) {
                FormSeparator(inset = separatorInset)
            }
        }
    }
}

/** The 0.5px inset hairline between form rows. */
@Composable
private fun FormSeparator(inset: Dp) {
    val colors = CashizardTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = inset)
            .height(0.5.dp)
            .background(colors.separator),
    )
}
