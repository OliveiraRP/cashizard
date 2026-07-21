package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor

/** Pick an account or wallet to edit, grouped by account with standalone wallets last. */
@Composable
fun EditWalletsPickerScreen(
    state: WalletSheetUiState,
    onEditWallet: (String) -> Unit,
    onEditAccount: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val overview = state.overview ?: return
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = dimens.space20, bottom = dimens.space32),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        overview.accounts.forEach { entry ->
            GroupedWalletCard(
                header = entry.account.name,
                wallets = entry.wallets,
                onEditWallet = onEditWallet,
                // The header edits the account itself; "Other Wallets" below has none.
                onEditHeader = { onEditAccount(entry.account.id) },
            )
        }
        if (overview.standaloneWallets.isNotEmpty()) {
            GroupedWalletCard(
                header = "Other Wallets",
                wallets = overview.standaloneWallets,
                onEditWallet = onEditWallet,
                onEditHeader = null,
            )
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun GroupedWalletCard(
    header: String,
    wallets: List<Wallet>,
    onEditWallet: (String) -> Unit,
    onEditHeader: (() -> Unit)?,
) {
    val dimens = CashizardTheme.dimens
    FormCard(
        modifier = Modifier.padding(horizontal = dimens.listPadding),
        cornerRadius = dimens.radiusControl,
        separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
        rows = buildList {
            add { GroupHeaderRow(header, onEditHeader) }
            wallets.forEach { wallet ->
                add {
                    CashListRow(
                        title = wallet.name,
                        titleStyle = CashizardTheme.typography.bodyLarge,
                        minHeight = dimens.rowHeightCompact,
                        verticalPadding = dimens.space8,
                        showChevron = true,
                        onClick = { onEditWallet(wallet.id) },
                        leading = {
                            IconDisc(
                                icon = iconFor(wallet.icon),
                                style = IconDiscStyle.Neutral,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                    )
                }
            }
        },
    )
}

/**
 * The grouping heading: a bold label with no icon. Account headers are tappable
 * (to edit the account) and show the "Account" tag and a chevron; the
 * "Other Wallets" heading has no [onEdit] and is inert.
 */
@Composable
private fun GroupHeaderRow(label: String, onEdit: (() -> Unit)?) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.accountHeader)
            .then(
                if (onEdit != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onEdit,
                    )
                } else {
                    Modifier
                },
            )
            .defaultMinSize(minHeight = dimens.rowHeightCompact)
            .padding(horizontal = dimens.space16, vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space8),
    ) {
        Text(
            text = label,
            style = CashizardTheme.typography.bodyLarge,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (onEdit != null) {
            Text(
                text = "Account",
                style = CashizardTheme.typography.footnote,
                color = colors.textTertiary,
            )
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = colors.textPlaceholder,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
