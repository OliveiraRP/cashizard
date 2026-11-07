package com.houseofrafa.cashizard.presentation.feature.settings.connectbank

import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.AspspOption

/** The sheet's three steps, shown one at a time rather than as a push stack. */
enum class ConnectStep { PickAccount, PickBank, WaitingForBank }

data class ConnectBankAccountUiState(
    val accounts: List<Account> = emptyList(),
    val selectedAccountId: String? = null,
    val aspsps: List<AspspOption> = emptyList(),
    val bankQuery: String = "",
    val selectedAspsp: AspspOption? = null,
    val step: ConnectStep = ConnectStep.PickAccount,
    val loading: Boolean = true,
    val startingAuth: Boolean = false,
    val checkingLink: Boolean = false,
    val loadingBanks: Boolean = false,
    val errorMessage: String? = null,
    /** The sheet has asked to close; the host plays the dismiss animation. */
    val closeRequested: Boolean = false,
) {
    val selectedAccount: Account? get() = accounts.firstOrNull { it.id == selectedAccountId }

    val filteredAspsps: List<AspspOption>
        get() = if (bankQuery.isBlank()) {
            aspsps
        } else {
            aspsps.filter { it.name.contains(bankQuery, ignoreCase = true) }
        }

    val canContinueToBankPicker: Boolean get() = !loading && selectedAccountId != null

    val canStartAuth: Boolean get() = !startingAuth && selectedAspsp != null
}
