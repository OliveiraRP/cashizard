package com.houseofrafa.cashizard.presentation.feature.main

import kotlinx.serialization.Serializable

/** The tab destinations, as persisted by the child stack. */
@Serializable
sealed interface MainConfig {
    @Serializable
    data object Transactions : MainConfig

    @Serializable
    data object Wallets : MainConfig

    @Serializable
    data object Analytics : MainConfig
}

/** What the modal page sheet can be opened at. */
@Serializable
sealed interface SheetConfig {
    @Serializable
    data object AddTransaction : SheetConfig

    /**
     * Editing carries the transaction's fields as primitives so the config
     * stays trivially serializable and the form prefills without a refetch.
     */
    @Serializable
    data class EditTransaction(
        val transactionId: String,
        val type: String,
        val amountCents: Long,
        val fromWalletId: String?,
        val toWalletId: String?,
        val categoryId: String?,
        val occurredOn: String,
        val note: String,
    ) : SheetConfig

    @Serializable
    data object AddWallet : SheetConfig

    @Serializable
    data object AddAccount : SheetConfig

    @Serializable
    data object EditWallets : SheetConfig

    @Serializable
    data class WalletDetails(val walletId: String) : SheetConfig

    @Serializable
    data object FilterCategories : SheetConfig
}
