package com.houseofrafa.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.houseofrafa.core.ui.compose.component.AppButton
import com.houseofrafa.core.ui.compose.component.AppTextField
import com.houseofrafa.core.ui.theme.CashizardTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CashizardTheme.colors.backgroundPrimary),
        contentAlignment = Alignment.Center,
    ) {
        if (uiState.isValidating || uiState.navigateToHome) {
            CircularProgressIndicator(
                modifier    = Modifier.size(32.dp),
                color       = CashizardTheme.colors.brandPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .safeContentPadding()
                    .padding(horizontal = CashizardTheme.spacing.screenHorizontal),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text      = "Enter your access token to continue",
                    style     = CashizardTheme.typography.bodyMedium,
                    color     = CashizardTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(CashizardTheme.spacing.xxl))

                AppTextField(
                    value           = uiState.token,
                    onValueChange   = viewModel::onTokenChange,
                    placeholder     = "Token",
                    isPassword      = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { viewModel.login() }),
                    errorMessage    = uiState.errorMessage,
                    modifier        = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(CashizardTheme.spacing.md))

                AppButton(
                    text      = "Continue",
                    onClick   = viewModel::login,
                    enabled   = uiState.token.isNotBlank(),
                    isLoading = uiState.isLoading,
                    modifier  = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

