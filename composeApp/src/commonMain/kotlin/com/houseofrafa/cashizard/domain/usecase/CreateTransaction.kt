package com.houseofrafa.cashizard.domain.usecase

import com.houseofrafa.cashizard.domain.model.NewTransaction
import com.houseofrafa.cashizard.domain.model.Transaction
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.repository.TransactionRepository

/**
 * Creates a transaction, checking locally what the schema's `chk_wallets_by_type`
 * and `amount > 0` constraints enforce server-side — so an invalid form fails fast
 * with a readable message instead of a Postgres error.
 */
class CreateTransaction(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(command: NewTransaction): Transaction {
        require(command.amount.cents > 0) { "Amount must be greater than zero." }

        when (command.type) {
            TransactionType.EXPENSE -> {
                require(command.fromWalletId != null) { "An expense needs a source wallet." }
                require(command.toWalletId == null) { "An expense cannot have a destination wallet." }
                require(command.categoryId != null) { "An expense needs a category." }
            }

            TransactionType.INCOME -> {
                require(command.toWalletId != null) { "Income needs a destination wallet." }
                require(command.fromWalletId == null) { "Income cannot have a source wallet." }
                require(command.categoryId != null) { "Income needs a category." }
            }

            TransactionType.TRANSFER -> {
                require(command.fromWalletId != null && command.toWalletId != null) {
                    "A transfer needs both a source and a destination wallet."
                }
                require(command.fromWalletId != command.toWalletId) {
                    "A transfer needs two different wallets."
                }
            }
        }

        return transactionRepository.createTransaction(command)
    }
}
