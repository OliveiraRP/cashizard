package com.houseofrafa.cashizard.presentation.feature.wallets.addwallet

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
 * Hosts the "New Wallet" sheet's internal push stack.
 *
 * Navigation only: picking a value writes it to [viewModel] and then pops, so
 * the form never has to hand results back up the stack.
 */
class AddWalletSheetComponent(
    componentContext: ComponentContext,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<AddWalletSheetConfig>()

    val viewModel: AddWalletViewModel = koinViewModel()

    val stack: Value<ChildStack<*, AddWalletSheetConfig>> = childStack(
        source = navigation,
        serializer = AddWalletSheetConfig.serializer(),
        initialConfiguration = AddWalletSheetConfig.Form,
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

    fun onPickIcon() = navigation.pushNew(AddWalletSheetConfig.IconPicker)

    fun onPickType() = navigation.pushNew(AddWalletSheetConfig.TypePicker)

    fun onIconSelected(icon: String) {
        viewModel.onIconChange(icon)
        navigation.pop()
    }

    fun onTypeSelected(type: WalletType) {
        viewModel.onTypeChange(type)
        navigation.pop()
    }
}
