package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import com.houseofrafa.cashizard.domain.model.Account

/** The edit-account form. Accounts carry only a name, so that is all it holds. */
data class EditAccountUiState(
    val accountId: String,
    val name: String,
    val saving: Boolean = false,
    val archiving: Boolean = false,
    val errorMessage: String? = null,
) {
    val busy: Boolean get() = saving || archiving
    val canSave: Boolean get() = !busy && name.isNotBlank()

    companion object {
        fun from(account: Account): EditAccountUiState =
            EditAccountUiState(accountId = account.id, name = account.name)
    }
}
