package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetBackButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetCloseButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetScaffold
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.feature.wallets.WalletTypePicker

/**
 * Renders the wallet sheet's inner stack with an iOS push. The root gets a close
 * button; the detail adds an edit pencil, the edit form a save check, and pushed
 * lists and pickers a back chevron.
 */
@Composable
fun WalletSheetContent(
    component: WalletSheetComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.stack.subscribeAsState()
    val state by component.viewModel.state.collectAsState()
    val isRoot = stack.backStack.isEmpty()
    val active = stack.active.instance

    val title = when (active) {
        WalletSheetConfig.EditWalletsPicker -> "Edit Wallets"
        is WalletSheetConfig.WalletDetails -> "Wallet"
        is WalletSheetConfig.EditWallet -> "Edit Wallet"
        is WalletSheetConfig.EditAccount -> "Edit Account"
        is WalletSheetConfig.AllTransactions -> "Transactions"
        WalletSheetConfig.IconPicker -> "Choose Icon"
        WalletSheetConfig.TypePicker -> "Wallet Type"
    }

    SheetScaffold(
        title = title,
        modifier = modifier,
        leading = {
            if (isRoot) {
                SheetCloseButton(onClick = component::onCloseClick)
            } else {
                SheetBackButton(onClick = component::onBackClick)
            }
        },
        trailing = walletSheetTrailing(active, state, component),
    ) {
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = CashizardTheme.colors.accent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(32.dp),
                )
            }
            return@SheetScaffold
        }

        state.errorMessage?.let { message ->
            Box(
                Modifier.fillMaxSize().padding(CashizardTheme.dimens.space32),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = message,
                    style = CashizardTheme.typography.footnote,
                    color = CashizardTheme.colors.textTertiary,
                    textAlign = TextAlign.Center,
                )
            }
            return@SheetScaffold
        }

        Children(
            stack = component.stack,
            modifier = Modifier.fillMaxWidth().weight(1f),
            animation = stackAnimation(slide()),
        ) { created ->
            WalletSheetChild(component = component, state = state, config = created.instance)
        }
    }
}

@Composable
private fun WalletSheetChild(
    component: WalletSheetComponent,
    state: WalletSheetUiState,
    config: WalletSheetConfig,
) {
    when (config) {
        WalletSheetConfig.EditWalletsPicker ->
            EditWalletsPickerScreen(
                state = state,
                onEditWallet = component::onEditWallet,
                onEditAccount = component::onEditAccount,
            )

        is WalletSheetConfig.WalletDetails ->
            state.wallet(config.walletId)?.let { wallet ->
                WalletDetailScreen(
                    wallet = wallet,
                    accountName = state.account(wallet.accountId)?.name,
                    recent = state.recentTransactions(config.walletId, limit = 5),
                    onSeeAll = { component.onSeeAllTransactions(config.walletId) },
                )
            }

        is WalletSheetConfig.EditWallet ->
            state.editForm?.let { form ->
                EditWalletScreen(
                    form = form,
                    accounts = state.accounts,
                    onNameChange = component.viewModel::onNameChange,
                    onPickIcon = component::onPickIcon,
                    onPickType = component::onPickType,
                    onTypeAmountChange = component.viewModel::onTypeAmountChange,
                    onAccountToggled = component.viewModel::onAccountToggled,
                    onArchive = component.viewModel::onArchive,
                )
            }

        is WalletSheetConfig.EditAccount ->
            state.accountForm?.let { form ->
                EditAccountScreen(
                    form = form,
                    onNameChange = component.viewModel::onAccountNameChange,
                    onArchive = component.viewModel::onArchiveAccount,
                )
            }

        is WalletSheetConfig.AllTransactions ->
            WalletTransactionsScreen(days = state.walletFeed(config.walletId))

        WalletSheetConfig.IconPicker ->
            state.editForm?.let { form ->
                EditWalletIconPicker(form = form, onIconSelected = component::onIconSelected)
            }

        WalletSheetConfig.TypePicker ->
            state.editForm?.let { form ->
                WalletTypePicker(selected = form.type, onSelect = component::onTypeSelected)
            }
    }
}

/** The header's trailing control, which differs per stack entry. */
private fun walletSheetTrailing(
    active: WalletSheetConfig,
    state: WalletSheetUiState,
    component: WalletSheetComponent,
): (@Composable () -> Unit)? = when (active) {
    is WalletSheetConfig.WalletDetails -> {
        {
            CircleIconButton(
                icon = Lucide.Pencil,
                onClick = { component.onEditWallet(active.walletId) },
                contentDescription = "Edit wallet",
                iconSize = 18.dp,
            )
        }
    }

    is WalletSheetConfig.EditWallet -> {
        {
            CircleIconButton(
                icon = Lucide.Check,
                onClick = component.viewModel::onSaveEdit,
                contentDescription = "Save",
                style = CircleButtonStyle.Accent,
                enabled = state.editForm?.canSave == true,
            )
        }
    }

    is WalletSheetConfig.EditAccount -> {
        {
            CircleIconButton(
                icon = Lucide.Check,
                onClick = component.viewModel::onSaveAccount,
                contentDescription = "Save",
                style = CircleButtonStyle.Accent,
                enabled = state.accountForm?.canSave == true,
            )
        }
    }

    else -> null
}
