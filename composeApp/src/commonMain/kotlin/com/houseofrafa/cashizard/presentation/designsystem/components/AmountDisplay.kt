package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * The large amount readout at the top of the add-transaction sheet: the typed
 * figure in tabular digits with a dimmed currency suffix.
 */
@Composable
fun AmountDisplay(
    amount: String,
    modifier: Modifier = Modifier,
    currency: String = "€",
) {
    val colors = CashizardTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        // Both sit on a shared baseline. Aligning their boxes instead would drop
        // the currency: it is the smaller of the two, so it has the shallower
        // descender, and a common bottom edge would push its baseline lower.
        Text(
            text = amount,
            style = CashizardTheme.typography.amountHero,
            color = colors.textPrimary,
            maxLines = 1,
            modifier = Modifier.alignByBaseline(),
        )
        Text(
            text = " $currency",
            style = CashizardTheme.typography.amountHero.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = colors.textQuaternary,
            maxLines = 1,
            modifier = Modifier.alignByBaseline(),
        )
    }
}
