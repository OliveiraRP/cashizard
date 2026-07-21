package com.houseofrafa.cashizard.presentation.feature.wallets.addaccount

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.houseofrafa.cashizard.presentation.arch.koinViewModel

/**
 * Hosts the "New Wallet Account" sheet. It has no internal stack, so the only
 * navigation it owns is turning a back press into a close request.
 */
class AddAccountSheetComponent(
    componentContext: ComponentContext,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext {

    val viewModel: AddAccountViewModel = koinViewModel()

    init {
        backHandler.register(BackCallback { viewModel.onRequestClose() })
    }

    /** The dismiss animation finished; drop the sheet from the slot. */
    fun onDismissed() = onDismiss()
}
