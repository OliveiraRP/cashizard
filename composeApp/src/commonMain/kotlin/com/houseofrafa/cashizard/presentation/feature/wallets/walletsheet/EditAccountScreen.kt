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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.common.PickerRow
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetTextRow
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import com.houseofrafa.cashizard.presentation.feature.categories.SelectionMark

/**
 * "Edit Account": a name, a default wallet for auto-imported bank
 * transactions, and an archive action. Archiving deletes the account, which
 * the FK leaves its wallets standalone rather than removing them.
 */
@Composable
fun EditAccountScreen(
    form: EditAccountUiState,
    onNameChange: (String) -> Unit,
    onDefaultWalletChange: (String) -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    var walletPickerExpanded by remember { mutableStateOf(false) }

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

        if (form.walletOptions.isNotEmpty()) {
            SectionHeader(
                title = "Default Wallet",
                style = CashizardTheme.typography.sectionLabelSmall,
            )
            FormCard(
                modifier = Modifier.padding(horizontal = dimens.listPadding),
                cornerRadius = dimens.radiusControl,
                separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
                rows = buildList {
                    val selected = form.walletOptions.firstOrNull { it.id == form.defaultWalletId }
                    add {
                        PickerRow(
                            label = "Default Wallet",
                            value = selected?.name ?: "Choose",
                            onClick = { walletPickerExpanded = !walletPickerExpanded },
                            leading = {
                                IconDisc(
                                    icon = iconFor(selected?.icon ?: "wallet"),
                                    style = IconDiscStyle.Neutral,
                                    size = dimens.iconDiscWallet,
                                    iconSize = dimens.space16,
                                )
                            },
                        )
                    }
                    if (walletPickerExpanded) {
                        form.walletOptions.forEach { wallet ->
                            add {
                                CashListRow(
                                    title = wallet.name,
                                    onClick = {
                                        onDefaultWalletChange(wallet.id)
                                        walletPickerExpanded = false
                                    },
                                    leading = {
                                        IconDisc(
                                            icon = iconFor(wallet.icon),
                                            style = IconDiscStyle.Neutral,
                                            size = dimens.iconDiscWallet,
                                            iconSize = dimens.space16,
                                        )
                                    },
                                    trailing = { SelectionMark(wallet.id == form.defaultWalletId) },
                                )
                            }
                        }
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
