package com.houseofrafa.core.ui.compose.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.houseofrafa.core.ui.theme.CashizardTheme

@Composable
fun MainScreenTemplate(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CashizardTheme.colors.backgroundPrimary)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = CashizardTheme.spacing.screenHorizontal),
        content = content,
    )
}
