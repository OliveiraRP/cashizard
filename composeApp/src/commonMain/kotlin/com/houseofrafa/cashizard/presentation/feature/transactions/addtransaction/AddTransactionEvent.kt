package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

/** One-shot outcomes the UI, not the ViewModel, turns into navigation. */
sealed interface AddTransactionEvent {
    /**
     * A category or group form saved successfully, so its stack entry should
     * pop back to whatever pushed it.
     */
    data object FormSaved : AddTransactionEvent
}
