package com.houseofrafa.core.ui.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.houseofrafa.core.ui.icon.AppIcons
import com.houseofrafa.core.ui.theme.CashizardTheme

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    errorMessage: String? = null,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val resolvedKeyboardOptions = if (isPassword) {
        keyboardOptions.copy(keyboardType = KeyboardType.Password)
    } else {
        keyboardOptions
    }

    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text  = placeholder,
                    style = CashizardTheme.typography.bodyLarge,
                    color = CashizardTheme.colors.textTertiary,
                )
            },
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) AppIcons.Status.VisibilityOff
                                          else AppIcons.Status.Visibility,
                            contentDescription = if (passwordVisible) "Hide" else "Show",
                            tint = CashizardTheme.colors.textSecondary,
                        )
                    }
                }
            } else null,
            keyboardOptions = resolvedKeyboardOptions,
            keyboardActions = keyboardActions,
            singleLine  = true,
            textStyle   = CashizardTheme.typography.bodyLarge,
            shape       = CashizardTheme.shapes.large,
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = CashizardTheme.colors.surfacePrimary,
                unfocusedContainerColor = CashizardTheme.colors.surfacePrimary,
                focusedTextColor        = CashizardTheme.colors.textPrimary,
                unfocusedTextColor      = CashizardTheme.colors.textPrimary,
                cursorColor             = CashizardTheme.colors.brandPrimary,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor     = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        AnimatedVisibility(
            visible = errorMessage != null,
            enter   = fadeIn(),
            exit    = fadeOut(),
        ) {
            Text(
                text     = errorMessage.orEmpty(),
                style    = CashizardTheme.typography.labelSmall,
                color    = CashizardTheme.colors.feedbackError,
                modifier = Modifier.padding(
                    top   = CashizardTheme.spacing.xs,
                    start = CashizardTheme.spacing.xs,
                ),
            )
        }
    }
}