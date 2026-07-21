package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.domain.model.TransactionType
import kotlinx.datetime.LocalDate

/** The existing transaction an edit session is seeded from. */
data class EditingTransaction(
    val id: String,
    val type: TransactionType,
    val amount: Money,
    val fromWalletId: String?,
    val toWalletId: String?,
    val categoryId: String?,
    val occurredOn: LocalDate,
    val note: String,
)
