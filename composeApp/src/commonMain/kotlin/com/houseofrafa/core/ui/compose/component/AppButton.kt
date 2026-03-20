package com.houseofrafa.core.ui.compose.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.houseofrafa.core.ui.theme.CashizardTheme

@Composable
fun AppButton(
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
