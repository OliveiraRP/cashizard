package com.houseofrafa.cashizard.domain.model

import kotlinx.datetime.LocalDate

data class NewTransaction(
    val type: TransactionType,
    val amount: Money,
    val fromWalletId: String?,
    val toWalletId: String?,
    val categoryId: String?,
    val occurredOn: LocalDate,
    val note: String? = null,
)

/**
 * Edits to an existing transaction. The wallet side that a type does not use must
 * be null so the write satisfies the schema's chk_wallets_by_type.
 */
data class UpdateTransaction(
    val id: String,
    val type: TransactionType,
    val amount: Money,
    val fromWalletId: String?,
    val toWalletId: String?,
    val categoryId: String?,
    val occurredOn: LocalDate,
    val note: String? = null,
)
