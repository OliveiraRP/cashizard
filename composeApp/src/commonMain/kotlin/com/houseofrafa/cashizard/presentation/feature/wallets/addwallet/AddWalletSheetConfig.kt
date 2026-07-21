package com.houseofrafa.cashizard.presentation.feature.wallets.addwallet

import kotlinx.serialization.Serializable

/** The entries on the "New Wallet" sheet's internal push stack. */
@Serializable
sealed interface AddWalletSheetConfig {
    @Serializable
    data object Form : AddWalletSheetConfig

    @Serializable
    data object IconPicker : AddWalletSheetConfig

    @Serializable
    data object TypePicker : AddWalletSheetConfig
}
