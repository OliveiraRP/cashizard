package com.houseofrafa.cashizard.presentation.feature.settings.connectbank

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.houseofrafa.cashizard.presentation.arch.koinViewModel

/**
 * Hosts the "Connect Bank Account" sheet. No Decompose stack — the three steps
 * are one ViewModel-driven state machine, not pushed screens — so the only
 * navigation this owns is handing a back press to the ViewModel to decide
 * whether that steps back or closes the sheet.
 */
class ConnectBankAccountSheetComponent(
    componentContext: ComponentContext,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext {

    val viewModel: ConnectBankAccountViewModel = koinViewModel()

    init {
        backHandler.register(BackCallback { viewModel.onBackPressed() })
    }

    fun onDismissed() = onDismiss()
}
