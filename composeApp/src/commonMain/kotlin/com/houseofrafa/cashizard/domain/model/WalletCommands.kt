package com.houseofrafa.cashizard.domain.model

data class NewWallet(
    val spaceId: String,
    val accountId: String?,
    val name: String,
    val icon: String = "wallet",
    val type: WalletType = WalletType.EXPENSE,
    val goalAmount: Money? = null,
    val annualBudget: Money? = null,
    val initialBalance: Money = Money.Zero,
    val sortOrder: Int = 0,
)

/**
 * Edits to an existing wallet. [initialBalance] is deliberately absent — the
 * opening balance is set once at creation and never re-edited, and the design's
 * edit form omits it. The unused of [goalAmount]/[annualBudget] must be null so
 * the write satisfies the schema's chk_type_fields.
 */
data class UpdateWallet(
    val id: String,
    val accountId: String?,
    val name: String,
    val icon: String,
    val type: WalletType,
    val goalAmount: Money? = null,
    val annualBudget: Money? = null,
)
