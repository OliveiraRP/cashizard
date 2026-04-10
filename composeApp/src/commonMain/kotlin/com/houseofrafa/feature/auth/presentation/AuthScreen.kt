package com.houseofrafa.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.houseofrafa.core.ui.compose.components.LoadingButton
import com.houseofrafa.core.ui.compose.components.AppTextField
import com.houseofrafa.core.ui.compose.templates.MainScreenTemplate
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

    MainScreenTemplate {
        when {
            uiState.isValidating || uiState.navigateToHome -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(32.dp),
                        color       = CashizardTheme.colors.brandPrimary,
                        strokeWidth = 2.dp,
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (!uiState.isLoginMode) {
                        AppTextField(
                            value           = uiState.name,
                            onValueChange   = viewModel::onNameChange,
                            placeholder     = "Name",
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier        = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(CashizardTheme.spacing.sm))
                    }

                    AppTextField(
                        value           = uiState.email,
                        onValueChange   = viewModel::onEmailChange,
                        placeholder     = "Email",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction    = ImeAction.Next,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(CashizardTheme.spacing.sm))

                    AppTextField(
                        value           = uiState.password,
                        onValueChange   = viewModel::onPasswordChange,
                        placeholder     = "Password",
                        isPassword      = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Done,
                        ),
                        errorMessage = uiState.errorMessage,
                        modifier     = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(CashizardTheme.spacing.md))

                    LoadingButton(
                        text      = if (uiState.isLoginMode) "Log in" else "Sign up",
                        onClick   = viewModel::submit,
                        enabled   = uiState.email.isNotBlank() && uiState.password.isNotBlank() &&
                                (uiState.isLoginMode || uiState.name.isNotBlank()),
                        isLoading = uiState.isLoading,
                        modifier  = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(CashizardTheme.spacing.sm))

                    TextButton(onClick = viewModel::toggleMode) {
                        Text(
                            text  = if (uiState.isLoginMode) "Don't have an account? Sign up" else "Already have an account? Log in",
                            style = CashizardTheme.typography.bodySmall,
                            color = CashizardTheme.colors.brandPrimary,
                        )
                    }
                }
            }
        }
    }
}
