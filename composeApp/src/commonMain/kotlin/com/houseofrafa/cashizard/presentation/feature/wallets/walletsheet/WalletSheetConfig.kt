package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import kotlinx.serialization.Serializable

/** The entries on the wallet sheet's internal push stack. */
@Serializable
sealed interface WalletSheetConfig {
    @Serializable
    data object EditWalletsPicker : WalletSheetConfig

    @Serializable
    data class WalletDetails(val walletId: String) : WalletSheetConfig

    @Serializable
    data class EditWallet(val walletId: String) : WalletSheetConfig

    @Serializable
    data class EditAccount(val accountId: String) : WalletSheetConfig

    @Serializable
    data class AllTransactions(val walletId: String) : WalletSheetConfig

    @Serializable
    data object IconPicker : WalletSheetConfig

    @Serializable
    data object TypePicker : WalletSheetConfig
}
