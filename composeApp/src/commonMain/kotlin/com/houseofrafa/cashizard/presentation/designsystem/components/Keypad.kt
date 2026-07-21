package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Delete
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * The amount-entry keypad: digits 0-9, a decimal separator (European ',') and
 * backspace, on translucent rounded keys. Emits semantic callbacks; the caller
 * owns the amount buffer.
 */
@Composable
fun Keypad(
    onDigit: (Int) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    decimalSeparator: String = ",",
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(decimalSeparator, "0", BACKSPACE),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                row.forEach { key ->
                    KeypadKey(
                        key = key,
                        decimalSeparator = decimalSeparator,
                        onDigit = onDigit,
                        onDecimal = onDecimal,
                        onBackspace = onBackspace,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadKey(
    key: String,
    decimalSeparator: String,
    onDigit: (Int) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val isBackspace = key == BACKSPACE
    val onClick: () -> Unit = when {
        isBackspace -> onBackspace
        key == decimalSeparator -> onDecimal
        else -> key.toIntOrNull()?.let { digit -> { onDigit(digit) } } ?: {}
    }

    Box(
        modifier = modifier
            .height(dimens.keyHeight)
            .background(colors.fillKey, RoundedCornerShape(dimens.radiusKey))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isBackspace) {
            Icon(
                imageVector = Lucide.Delete,
                contentDescription = "Delete",
                tint = colors.textPrimary,
                modifier = Modifier.height(22.dp),
            )
        } else {
            Text(
                text = key,
                style = CashizardTheme.typography.body.copy(
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Normal,
                ),
                color = colors.textPrimary,
            )
        }
    }
}

/** Sentinel for the backspace key so it is not mistaken for a label. */
private const val BACKSPACE = "⌫"
