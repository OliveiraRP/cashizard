package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.presentation.common.IconPickerGrid
import com.houseofrafa.cashizard.presentation.common.PickerRow
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetRow
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetTextRow
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import com.houseofrafa.cashizard.presentation.feature.wallets.label
import com.houseofrafa.cashizard.presentation.feature.wallets.typeIcon
import com.houseofrafa.cashizard.presentation.feature.wallets.WalletSelectionMark

@Composable
fun EditWalletScreen(
    form: EditWalletUiState,
    accounts: List<Account>,
    onNameChange: (String) -> Unit,
    onPickIcon: () -> Unit,
    onPickType: () -> Unit,
    onTypeAmountChange: (String) -> Unit,
    onAccountToggled: (String) -> Unit,
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
            modifier = Modifier.padding(horizontal = dimens.listPadding),
        )

        SectionHeader(title = "Icon & type", style = CashizardTheme.typography.sectionLabelSmall)
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = listOf(
                {
                    PickerRow(
                        label = "Icon",
                        value = null,
                        onClick = onPickIcon,
                        leading = {
                            IconDisc(
                                icon = iconFor(form.icon),
                                color = colors.accent,
                                style = IconDiscStyle.Solid,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                    )
                },
                {
                    PickerRow(
                        label = "Type",
                        value = form.type.label,
                        onClick = onPickType,
                        leading = {
                            IconDisc(
                                icon = form.type.typeIcon(),
                                style = IconDiscStyle.Neutral,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                    )
                },
            ),
        )

        if (form.needsGoalAmount || form.needsAnnualBudget) {
            SectionHeader(
                title = if (form.needsGoalAmount) "Goal amount" else "Annual budget",
                style = CashizardTheme.typography.sectionLabelSmall,
            )
            InsetTextRow(
                value = form.typeAmount.text,
                onValueChange = onTypeAmountChange,
                placeholder = "0,00",
                textStyle = CashizardTheme.typography.amount,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.padding(horizontal = dimens.listPadding),
            )
        }

        if (accounts.isNotEmpty()) {
            SectionHeader(
                title = "Linked account",
                style = CashizardTheme.typography.sectionLabelSmall,
            )
            FormCard(
                modifier = Modifier.padding(horizontal = dimens.listPadding),
                cornerRadius = dimens.radiusControl,
                rows = accounts.map { account ->
                    {
                        InsetRow(
                            label = account.name,
                            leadingIcon = Lucide.Landmark,
                            onClick = { onAccountToggled(account.id) },
                            trailing = { WalletSelectionMark(form.accountId == account.id) },
                        )
                    }
                },
            )
        }

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

        // Archive is destructive but reversible (the wallet is hidden, not
        // deleted), so it lives at the foot of the form as a plain red action.
        DangerRowButton(
            label = "Archive Wallet",
            enabled = !form.busy,
            onClick = onArchive,
            modifier = Modifier.padding(top = 26.dp),
        )

        Spacer(Modifier.height(dimens.space32).navigationBarsPadding())
    }
}

/** The icon picker pushed from the edit form. */
@Composable
fun EditWalletIconPicker(form: EditWalletUiState, onIconSelected: (String) -> Unit) {
    IconPickerGrid(selected = form.icon, onIconSelected = onIconSelected)
}
