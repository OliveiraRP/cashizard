package com.houseofrafa.cashizard.domain.model

import kotlin.time.Instant
import kotlinx.datetime.LocalDate

/**
 * A single transaction. [amount] is always positive; [type] gives direction:
 *  - expense: [fromWalletId] set, [categoryId] required unless [source] is BANK
 *  - income:  [toWalletId] set, [categoryId] required unless [source] is BANK
 *  - transfer: both wallets set, [categoryId] optional
 *
 * A bank-imported row ([source] = BANK) may have a null [categoryId] until it is
 * matched by hand — the UI shows that as "Uncategorized". [externalRef] dedupes a
 * re-fetched import of the same bank transaction. [description] is the bank's own
 * clean label for the transaction (only ever set by the sync job) — kept separate
 * from [note], which stays the user's own free-text field.
 */
data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Money,
    val fromWalletId: String?,
    val toWalletId: String?,
    val categoryId: String?,
    val occurredOn: LocalDate,
    val note: String?,
    val description: String?,
    val recurringRuleId: String?,
    val source: TransactionSource,
    val externalRef: String?,
    val createdBy: String,
    val createdAt: Instant,
)
