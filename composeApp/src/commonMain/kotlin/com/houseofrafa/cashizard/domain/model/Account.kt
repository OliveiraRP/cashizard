package com.houseofrafa.cashizard.domain.model

/**
 * A real-world account (e.g. "ING · Main Account") with its reconciliation
 * [balance] from the `account_balances` view (sum of its non-archived wallets).
 * [defaultWalletId] is where auto-imported bank transactions land until
 * categorized; set automatically to the account's first wallet, changeable
 * from the Edit Account screen.
 */
data class Account(
    val id: String,
    val spaceId: String,
    val name: String,
    val icon: String,
    val sortOrder: Int,
    val balance: Money,
    val defaultWalletId: String?,
)
