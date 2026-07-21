package com.houseofrafa.cashizard.domain.model

/**
 * A wallet with its computed [balance] from the `wallet_balances` view.
 * [accountId] null = standalone. [spentThisYear] and [annualBudgetLeft] are only
 * present for budget wallets (null otherwise).
 */
data class Wallet(
    val id: String,
    val spaceId: String,
    val accountId: String?,
    val name: String,
    val icon: String,
    val type: WalletType,
    val goalAmount: Money?,
    val annualBudget: Money?,
    val sortOrder: Int,
    val archived: Boolean,
    val balance: Money,
    val spentThisYear: Money?,
    val annualBudgetLeft: Money?,
) {
    /** Completion fraction (0f..1f) for goal wallets; 0 when no/zero goal. */
    val goalProgress: Float
        get() {
            val goal = goalAmount ?: return 0f
            if (goal.cents <= 0) return 0f
            return (balance.cents.toFloat() / goal.cents.toFloat()).coerceIn(0f, 1f)
        }
}
