package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.presentation.common.AmountBuffer
import com.houseofrafa.cashizard.presentation.common.toAmountBuffer

/** The edit-wallet form. Prefilled from the wallet; the opening balance is not edited. */
data class EditWalletUiState(
    val walletId: String,
    val name: String,
    val icon: String,
    val type: WalletType,
    /** Goal target or annual budget, depending on [type]. */
    val typeAmount: AmountBuffer,
    val accountId: String?,
    val saving: Boolean = false,
    val archiving: Boolean = false,
    val errorMessage: String? = null,
) {
    val needsGoalAmount: Boolean get() = type == WalletType.GOAL
    val needsAnnualBudget: Boolean get() = type == WalletType.BUDGET

    val busy: Boolean get() = saving || archiving

    /** Mirrors the schema's chk_type_fields and its `> 0` checks. */
    val canSave: Boolean
        get() = !busy && name.isNotBlank() && when (type) {
            WalletType.GOAL, WalletType.BUDGET -> typeAmount.toMoney().cents > 0
            else -> true
        }

    companion object {
        fun from(wallet: Wallet): EditWalletUiState = EditWalletUiState(
            walletId = wallet.id,
            name = wallet.name,
            icon = wallet.icon,
            type = wallet.type,
            typeAmount = when (wallet.type) {
                WalletType.GOAL -> wallet.goalAmount
                WalletType.BUDGET -> wallet.annualBudget
                else -> null
            }?.toAmountBuffer() ?: AmountBuffer.Empty,
            accountId = wallet.accountId,
        )
    }
}
