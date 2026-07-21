package com.houseofrafa.cashizard.presentation.feature.wallets.addwallet

import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.presentation.common.AmountBuffer

data class AddWalletUiState(
    val name: String = "",
    val icon: String = "wallet",
    val type: WalletType = WalletType.EXPENSE,
    /** Goal target or annual budget, depending on [type]. */
    val typeAmount: AmountBuffer = AmountBuffer.Empty,
    val startingBalance: AmountBuffer = AmountBuffer.Empty,
    /** Optional: `wallets.account_id` is nullable, so no account is a valid state. */
    val accountId: String? = null,
    val accounts: List<Account> = emptyList(),
    val loading: Boolean = true,
    val saving: Boolean = false,
    val errorMessage: String? = null,
    /** The sheet has asked to close; the host plays the dismiss animation. */
    val closeRequested: Boolean = false,
) {
    val needsGoalAmount: Boolean get() = type == WalletType.GOAL
    val needsAnnualBudget: Boolean get() = type == WalletType.BUDGET

    /** Mirrors the schema's chk_type_fields and its `> 0` checks. */
    val canSave: Boolean
        get() = !saving && name.isNotBlank() && when (type) {
            WalletType.GOAL, WalletType.BUDGET -> typeAmount.toMoney().cents > 0
            else -> true
        }
}
