package com.houseofrafa.cashizard.presentation.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.User
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.FormFieldRow
import com.houseofrafa.cashizard.presentation.designsystem.components.PrimaryButton

@Composable
fun SignUpScreen(viewModel: SignUpViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    // Confirmation-required is a one-shot outcome, not state: navigating is the
    // UI's job, so the ViewModel only reports that it happened.
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SignUpEvent.ConfirmationRequired -> onBack()
            }
        }
    }
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .safeDrawingPadding()
            .imePadding(),
    ) {
        Box(modifier = Modifier.padding(horizontal = dimens.screenPadding, vertical = dimens.space8)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(colors.surfaceChip, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Lucide.ChevronLeft,
                    contentDescription = "Back",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, bottom = 36.dp)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Lucide.Sparkles,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.height(dimens.space8))
            Text(
                text = "Create your account",
                style = CashizardTheme.typography.displayHeadline,
                color = colors.textPrimary,
            )
        }

        Column(modifier = Modifier.padding(horizontal = dimens.screenPadding)) {
            FormCard(
                rows = listOf(
                    {
                        FormFieldRow(
                            value = state.name,
                            onValueChange = viewModel::onNameChange,
                            leadingIcon = Lucide.User,
                            placeholder = "Name",
                            enabled = !state.submitting,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        )
                    },
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
                visible = state.errorMessage == null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = "At least ${SignUpUiState.MIN_PASSWORD_LENGTH} characters.",
                    style = CashizardTheme.typography.caption,
                    color = colors.textQuaternary,
                    modifier = Modifier.padding(top = dimens.space8, start = 6.dp),
                )
            }

            Spacer(Modifier.height(dimens.space20))

            PrimaryButton(
                text = "Create account",
                onClick = viewModel::onSubmit,
                enabled = state.canSubmit,
                loading = state.submitting,
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.screenPadding)
                .padding(bottom = 54.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Already have an account? ",
                style = CashizardTheme.typography.bodySmall,
                color = colors.textTertiary,
            )
            Text(
                text = "Log in",
                style = CashizardTheme.typography.bodySmallStrong,
                color = colors.accent,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBack,
                ),
            )
        }
    }
}
