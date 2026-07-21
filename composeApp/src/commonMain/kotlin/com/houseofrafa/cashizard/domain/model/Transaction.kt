package com.houseofrafa.cashizard.domain.model

import kotlin.time.Instant
import kotlinx.datetime.LocalDate

/**
 * A single transaction. [amount] is always positive; [type] gives direction:
 *  - expense: [fromWalletId] set, [categoryId] required
 *  - income:  [toWalletId] set, [categoryId] required
 *  - transfer: both wallets set, [categoryId] optional
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
    val recurringRuleId: String?,
    val createdBy: String,
    val createdAt: Instant,
)
