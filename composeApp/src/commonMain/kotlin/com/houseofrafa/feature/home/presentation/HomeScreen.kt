package com.houseofrafa.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.houseofrafa.core.ui.theme.CashizardTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CashizardTheme.colors.backgroundPrimary)
            .safeContentPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = "Hello, ${viewModel.user?.name}!",
            style = CashizardTheme.typography.headlineLarge,
            color = CashizardTheme.colors.textPrimary,
        )
    }
}