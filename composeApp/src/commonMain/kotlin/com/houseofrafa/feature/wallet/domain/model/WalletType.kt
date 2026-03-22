package com.houseofrafa.feature.wallet.domain.model

enum class WalletType {
    EXPENSE, SAVINGS, BUDGET, GOAL;

    companion object {
        fun fromString(value: String): WalletType = when (value.lowercase()) {
            "expense" -> EXPENSE
            "savings" -> SAVINGS
            "budget"  -> BUDGET
            "goal"    -> GOAL
            else      -> BUDGET
        }
    }
}
