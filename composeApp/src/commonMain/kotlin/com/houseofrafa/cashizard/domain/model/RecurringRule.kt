package com.houseofrafa.cashizard.domain.model

import kotlinx.datetime.LocalDate

/**
 * A monthly recurring rule. The server materializes it into transactions via
 * pg_cron; the app only does CRUD here. [dayOfMonth] is 1..31 (clamped to shorter
 * months server-side).
 */
data class RecurringRule(
    val id: String,
    val type: TransactionType,
    val amount: Money,
    val fromWalletId: String?,
    val toWalletId: String?,
    val categoryId: String?,
    val note: String?,
    val dayOfMonth: Int,
    val nextRun: LocalDate,
    val active: Boolean,
    val createdBy: String,
)
