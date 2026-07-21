package com.houseofrafa.cashizard.presentation.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.Sparkles
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.FormFieldRow
import com.houseofrafa.cashizard.presentation.designsystem.components.PrimaryButton

@Composable
fun LoginScreen(viewModel: LoginViewModel, onSignUpClick: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .safeDrawingPadding()
            .imePadding(),
    ) {
        // Brand block takes the free space above the form.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Lucide.Sparkles,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(30.dp),
            )
            Spacer(Modifier.height(dimens.space12))
            Text(
                text = "Cashizard",
                style = CashizardTheme.typography.displayTitle,
                color = colors.textPrimary,
            )
        }

        Column(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            FormCard(
                rows = listOf(
                    {
                        FormFieldRow(
                            value = state.email,
                            onValueChange = viewModel::onEmailChange,
                            leadingIcon = Lucide.Mail,
                            placeholder = "Email",
                            enabled = !state.submitting,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                        )
                    },
                    {
                        FormFieldRow(
                            value = state.password,
                            onValueChange = viewModel::onPasswordChange,
                            leadingIcon = Lucide.Lock,
                            placeholder = "Password",
                            isPassword = true,
                            enabled = !state.submitting,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(onDone = { viewModel.onSubmit() }),
                        )
                    },
                ),
            )

            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FormMessage(text = state.errorMessage.orEmpty(), isError = true)
            }

            AnimatedVisibility(
                visible = state.infoMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FormMessage(text = state.infoMessage.orEmpty(), isError = false)
            }

            Spacer(Modifier.height(dimens.space20))

            PrimaryButton(
                text = "Log in",
                onClick = viewModel::onSubmit,
                enabled = state.canSubmit,
                loading = state.submitting,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = dimens.screenPadding, end = dimens.screenPadding)
                .padding(bottom = 54.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Don't have an account? ",
                style = CashizardTheme.typography.bodySmall,
                color = colors.textTertiary,
            )
            Text(
                text = "Sign up",
                style = CashizardTheme.typography.bodySmallStrong,
                color = colors.accent,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSignUpClick,
                ),
            )
        }
    }
}

/** Inline message below a form card: small glyph plus copy, error or neutral. */
@Composable
internal fun FormMessage(text: String, isError: Boolean) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimens.space8, start = 6.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isError) {
            Icon(
                imageVector = Lucide.CircleAlert,
                contentDescription = null,
                tint = colors.errorText,
                modifier = Modifier.size(15.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = CashizardTheme.typography.footnote,
            color = if (isError) colors.errorText else colors.textTertiary,
        )
    }
}
