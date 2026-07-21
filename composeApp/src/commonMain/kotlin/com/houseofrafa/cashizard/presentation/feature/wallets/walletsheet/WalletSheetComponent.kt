package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.presentation.arch.koinViewModel

/**
 * Hosts the wallet page sheet: the wallet detail, the edit-wallets picker, the
 * edit forms and a wallet's full transaction list on one internal stack. Opened
 * either at the picker (from the ⋯ menu) or at a wallet's detail (from tapping
 * it), depending on [initialConfig].
 *
 * Navigation only: the sheet's state lives in [viewModel].
 */
class WalletSheetComponent(
    componentContext: ComponentContext,
    initialConfig: WalletSheetConfig,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<WalletSheetConfig>()

    val viewModel: WalletSheetViewModel = koinViewModel()

    val stack: Value<ChildStack<*, WalletSheetConfig>> = childStack(
        source = navigation,
        serializer = WalletSheetConfig.serializer(),
        initialConfiguration = initialConfig,
        handleBackButton = false,
        childFactory = { config, _ -> config },
    )

    init {
        backHandler.register(
            BackCallback {
                if (stack.value.backStack.isEmpty()) viewModel.onRequestClose() else navigation.pop()
            },
        )
    }

    fun onCloseClick() = viewModel.onRequestClose()

    fun onBackClick() = navigation.pop()

    fun onDismissed() = onDismiss()

    /** From the picker or the detail's edit button: open the form for a wallet. */
    fun onEditWallet(walletId: String) {
        if (viewModel.startEditingWallet(walletId)) {
            navigation.pushNew(WalletSheetConfig.EditWallet(walletId))
        }
    }

    /** From the picker's account header: open the account's edit form. */
    fun onEditAccount(accountId: String) {
        if (viewModel.startEditingAccount(accountId)) {
            navigation.pushNew(WalletSheetConfig.EditAccount(accountId))
        }
    }

    fun onSeeAllTransactions(walletId: String) =
        navigation.pushNew(WalletSheetConfig.AllTransactions(walletId))

    fun onPickIcon() = navigation.pushNew(WalletSheetConfig.IconPicker)

    fun onPickType() = navigation.pushNew(WalletSheetConfig.TypePicker)

    fun onIconSelected(icon: String) {
        viewModel.onIconChange(icon)
        navigation.pop()
    }

    fun onTypeSelected(type: WalletType) {
        viewModel.onTypeChange(type)
        navigation.pop()
    }
}
