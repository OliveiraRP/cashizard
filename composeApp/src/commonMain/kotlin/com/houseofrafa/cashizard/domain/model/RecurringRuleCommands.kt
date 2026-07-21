package com.houseofrafa.cashizard.domain.model

import kotlinx.datetime.LocalDate

data class NewRecurringRule(
    val type: TransactionType,
    val amount: Money,
    val fromWalletId: String?,
    val toWalletId: String?,
    val categoryId: String?,
    val note: String?,
    val dayOfMonth: Int,
    val nextRun: LocalDate,
)
