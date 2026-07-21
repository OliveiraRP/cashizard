package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * A text input row inside a [FormCard]: leading glyph, value, and — for password
 * fields — a reveal toggle. Carries no background or border of its own; the card
 * provides the surface.
 */
@Composable
fun FormFieldRow(
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    var revealed by remember { mutableStateOf(false) }

    val resolvedOptions = if (isPassword) {
        keyboardOptions.copy(keyboardType = KeyboardType.Password)
    } else {
        keyboardOptions
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimens.rowHeight)
            .padding(horizontal = dimens.space16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = colors.textQuaternary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(dimens.space12))

        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = CashizardTheme.typography.bodyLarge,
                    color = colors.textPlaceholder,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                textStyle = CashizardTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.accent),
                visualTransformation = if (isPassword && !revealed) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                keyboardOptions = resolvedOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (isPassword) {
            Spacer(Modifier.width(dimens.space12))
            Icon(
                imageVector = if (revealed) Lucide.EyeOff else Lucide.Eye,
                contentDescription = if (revealed) "Hide password" else "Show password",
                tint = colors.textPlaceholder,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { revealed = !revealed },
                    ),
            )
        }
    }
}
