package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetTextRow
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * "Edit Account": accounts carry only a name. Archiving deletes the account,
 * which the FK leaves its wallets standalone rather than removing them.
 */
@Composable
fun EditAccountScreen(
    form: EditAccountUiState,
    onNameChange: (String) -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        SectionHeader(title = "Name", style = CashizardTheme.typography.sectionLabelSmall)
        InsetTextRow(
            value = form.name,
            onValueChange = onNameChange,
            placeholder = "Account name",
            modifier = Modifier.padding(horizontal = dimens.listPadding),
        )

        if (form.errorMessage != null) {
            Text(
                text = form.errorMessage,
                style = CashizardTheme.typography.footnote,
                color = colors.errorText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.screenPadding, vertical = dimens.space12),
            )
        }

        // Deleting an account only unlinks its wallets (they become standalone),
        // so this reads as an archive rather than a destructive removal.
        DangerRowButton(
            label = "Archive Account",
            enabled = !form.busy,
            onClick = onArchive,
            modifier = Modifier.padding(top = 26.dp),
        )

        Spacer(Modifier.height(dimens.space32).navigationBarsPadding())
    }
}
