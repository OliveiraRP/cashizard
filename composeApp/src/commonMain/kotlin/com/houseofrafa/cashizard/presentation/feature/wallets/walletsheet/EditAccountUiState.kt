package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.Wallet

/**
 * The edit-account form. [defaultWalletId] is where auto-imported bank
 * transactions land; [walletOptions] are that account's own wallets to pick
 * from (empty until the account has at least one).
 */
data class EditAccountUiState(
    val accountId: String,
    val name: String,
    val defaultWalletId: String?,
    val walletOptions: List<Wallet> = emptyList(),
    val saving: Boolean = false,
    val archiving: Boolean = false,
    val errorMessage: String? = null,
) {
    val busy: Boolean get() = saving || archiving
    val canSave: Boolean get() = !busy && name.isNotBlank()

    companion object {
        fun from(account: Account, walletOptions: List<Wallet>): EditAccountUiState = EditAccountUiState(
            accountId = account.id,
            name = account.name,
            defaultWalletId = account.defaultWalletId,
            walletOptions = walletOptions,
        )
    }
}
