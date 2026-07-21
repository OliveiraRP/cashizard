package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.NewTransaction
import com.houseofrafa.cashizard.domain.model.Transaction
import com.houseofrafa.cashizard.domain.model.UpdateTransaction
import kotlinx.datetime.LocalDate

interface TransactionRepository {
    /** Transactions touching a space's wallets within [from]..[to] (inclusive). */
    suspend fun getTransactions(spaceId: String, from: LocalDate, to: LocalDate): List<Transaction>

    suspend fun createTransaction(command: NewTransaction): Transaction

    suspend fun updateTransaction(command: UpdateTransaction)

    suspend fun deleteTransaction(transactionId: String)
}
