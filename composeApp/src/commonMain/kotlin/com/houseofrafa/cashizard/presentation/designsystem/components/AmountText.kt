package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.format.formatEur

/** How an [AmountText] chooses its color. */
enum class AmountTone {
    /** Neutral primary text regardless of sign. */
    Neutral,

    /** Green when positive, red when negative (e.g. transaction lists). */
    Signed,

    /** Always the income green. */
    Positive,

    /** Always the expense red. */
    Negative,
}

/**
 * Renders a [Money] value with the single EUR formatter, tabular figures, and a
 * tone-based color. Screens never format money themselves.
 */
@Composable
fun AmountText(
    money: Money,
    modifier: Modifier = Modifier,
    style: TextStyle = CashizardTheme.typography.amount,
    tone: AmountTone = AmountTone.Neutral,
    withSign: Boolean = tone == AmountTone.Signed,
    showCents: Boolean = true,
    colorOverride: Color? = null,
) {
    val colors = CashizardTheme.colors
    val color = colorOverride ?: when (tone) {
        AmountTone.Neutral -> colors.textPrimary
        AmountTone.Signed -> if (money.isNegative) colors.negative else colors.positive
        AmountTone.Positive -> colors.positive
        AmountTone.Negative -> colors.negative
    }
    Text(
        text = money.formatEur(withSign = withSign, showCents = showCents),
        style = style,
        color = color,
        maxLines = 1,
        modifier = modifier,
    )
}
