package com.houseofrafa.cashizard.domain.usecase

import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.Transaction
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.repository.CategoryRepository
import com.houseofrafa.cashizard.domain.repository.TransactionRepository
import com.houseofrafa.cashizard.domain.repository.WalletRepository
import kotlinx.datetime.LocalDate

/**
 * A transaction joined with the display data a row needs. The DB stores only ids;
 * resolving them here keeps the presentation layer free of lookups.
 */
data class TransactionDetails(
    val transaction: Transaction,
    val category: Category?,
    val group: CategoryGroup?,
    val fromWallet: Wallet?,
    val toWallet: Wallet?,
) {
    /** Row title: the category, falling back to the transfer's destination. */
    val title: String
        get() = category?.name ?: toWallet?.name ?: fromWallet?.name.orEmpty()

    /** Icon name for the disc; transfers always use the fixed transfer icon. */
    val icon: String
        get() = category?.icon ?: "arrow-right-left"
}

/** Transactions of one calendar day, newest day first in the feed. */
data class TransactionDay(
    val date: LocalDate,
    val transactions: List<TransactionDetails>,
)

/**
 * Loads the transaction feed for a space and period, resolved and grouped by day.
 * Wallets and categories are fetched once and joined in memory rather than per row.
 */
class GetTransactionFeed(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(
        spaceId: String,
        from: LocalDate,
        to: LocalDate,
    ): List<TransactionDay> {
        val transactions = transactionRepository.getTransactions(spaceId, from, to)
        if (transactions.isEmpty()) return emptyList()

        // Archived wallets still own historical transactions, so include them.
        val walletsById = walletRepository.getWallets(spaceId, includeArchived = true)
            .associateBy { it.id }
        val groupsWithCategories = categoryRepository.getGroupsWithCategories(spaceId)
        val categoriesById = groupsWithCategories
            .flatMap { it.categories }
            .associateBy { it.id }
        val groupsById = groupsWithCategories.associate { it.group.id to it.group }

        return transactions
            .map { transaction ->
                val category = transaction.categoryId?.let { categoriesById[it] }
                TransactionDetails(
                    transaction = transaction,
                    category = category,
                    group = category?.let { groupsById[it.groupId] },
                    fromWallet = transaction.fromWalletId?.let { walletsById[it] },
                    toWallet = transaction.toWalletId?.let { walletsById[it] },
                )
            }
            .groupBy { it.transaction.occurredOn }
            .map { (date, items) -> TransactionDay(date, items) }
            .sortedByDescending { it.date }
    }
}
