package com.houseofrafa.cashizard.domain.model

/**
 * A real-world account (e.g. "ING · Main Account") with its reconciliation
 * [balance] from the `account_balances` view (sum of its non-archived wallets).
 */
data class Account(
    val id: String,
    val spaceId: String,
    val name: String,
    val icon: String,
    val sortOrder: Int,
    val balance: Money,
)
