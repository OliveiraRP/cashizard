package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.format.formatEur
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor

/**
 * Wallet selection for the From/To rows. Follows the wallets screen's row style
 * — neutral disc, regular title, balance trailing — with a check on the current
 * choice. For a transfer's destination, the source wallet is excluded because
 * the schema forbids both sides being the same.
 */
@Composable
fun WalletPickerScreen(
    component: AddTransactionSheetComponent,
    state: AddTransactionUiState,
    target: WalletTarget,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    // Only a transfer involves two wallets, so only there must the opposite side
    // be excluded. For income/expense the other id is a leftover preselection
    // (e.g. income keeps a stale fromWalletId) and must not hide a wallet.
    val excludedId = if (state.type == TransactionType.TRANSFER) {
        when (target) {
            WalletTarget.From -> state.toWalletId
            WalletTarget.To -> state.fromWalletId
        }
    } else {
        null
    }
    val wallets = state.wallets.filter { it.id != excludedId }
    val selectedId = when (target) {
        WalletTarget.From -> state.fromWalletId
        WalletTarget.To -> state.toWalletId
    }

    if (wallets.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().padding(dimens.space32),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No wallets available.",
                style = CashizardTheme.typography.footnote,
                color = colors.textTertiary,
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(
                start = dimens.listPadding,
                end = dimens.listPadding,
                top = dimens.space20,
                bottom = dimens.space32,
            ),
    ) {
        FormCard(
            cornerRadius = dimens.radiusCardLarge,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = wallets.map { wallet ->
                {
                    CashListRow(
                        title = wallet.name,
                        titleStyle = CashizardTheme.typography.bodyLarge,
                        minHeight = dimens.rowHeight,
                        verticalPadding = dimens.space4,
                        leading = {
                            IconDisc(
                                icon = iconFor(wallet.icon),
                                style = IconDiscStyle.Neutral,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                        trailing = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = wallet.balance.formatEur(),
                                    style = CashizardTheme.typography.amount,
                                    color = colors.textPrimary,
                                )
                                if (wallet.id == selectedId) {
                                    Icon(
                                        imageVector = Lucide.Check,
                                        contentDescription = "Selected",
                                        tint = colors.accent,
                                        modifier = Modifier
                                            .padding(start = dimens.space8)
                                            .size(18.dp),
                                    )
                                }
                            }
                        },
                        onClick = { component.onWalletSelected(target, wallet.id) },
                    )
                }
            },
        )
    }
}
