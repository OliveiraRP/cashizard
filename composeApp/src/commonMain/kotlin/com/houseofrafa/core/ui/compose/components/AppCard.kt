package com.houseofrafa.core.ui.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.houseofrafa.core.ui.theme.CashizardTheme

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    contentPadding: Dp = CashizardTheme.spacing.cardPadding,
    content: @Composable () -> Unit,
) {
    Card(
        modifier  = modifier,
        shape     = CashizardTheme.shapes.large,
        colors    = CardDefaults.cardColors(
            containerColor = CashizardTheme.colors.surfacePrimary,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
