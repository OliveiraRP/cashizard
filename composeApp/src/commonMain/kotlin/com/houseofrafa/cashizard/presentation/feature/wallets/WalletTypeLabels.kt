package com.houseofrafa.cashizard.presentation.feature.wallets

import com.houseofrafa.cashizard.domain.model.WalletType

/** Every wallet type, with the design's descriptions. */
val walletTypeOptions: List<Pair<WalletType, String>> = listOf(
    WalletType.EXPENSE to "Plain spending wallet, nothing extra",
    WalletType.GOAL to "Save toward a target amount",
    WalletType.BUDGET to "Annual budget, topped up monthly",
    WalletType.SAVINGS to "Counts toward savings & investments",
    WalletType.INVESTMENT to "Counts toward savings & investments",
)

/** The label shown for a wallet type on the Type row and picker. */
val WalletType.label: String
    get() = when (this) {
        WalletType.EXPENSE -> "Expense"
        WalletType.GOAL -> "Goal"
        WalletType.BUDGET -> "Budget"
        WalletType.SAVINGS -> "Savings"
        WalletType.INVESTMENT -> "Investment"
    }
