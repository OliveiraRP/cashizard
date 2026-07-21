package com.houseofrafa.cashizard.domain.usecase

import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.repository.CategoryRepository
import com.houseofrafa.cashizard.domain.repository.TransactionRepository
import kotlinx.datetime.LocalDate

/**
 * One donut slice and its legend row. [id] is a group id at the top level and a
 * category id when drilled into a group.
 */
data class SpendingSlice(
    val id: String,
    val name: String,
    val colorHex: String,
    val iconName: String,
    val total: Money,
    /** Share of [SpendingBreakdown.total], 0f..1f. */
    val fraction: Float,
)

data class SpendingBreakdown(
    val type: TransactionType,
    val total: Money,
    /** Largest first, as the donut and legend render them. */
    val slices: List<SpendingSlice>,
)

/**
 * Aggregates a period's transactions for the analytics donut — by category
 * group, or by category within [groupId] when drilling in.
 *
 * Transfers are excluded by design: they move money within the space rather
 * than spending or earning it, so [type] accepts only expense or income.
 */
class GetSpendingBreakdown(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(
        spaceId: String,
        from: LocalDate,
        to: LocalDate,
        type: TransactionType = TransactionType.EXPENSE,
        groupId: String? = null,
    ): SpendingBreakdown {
        require(type != TransactionType.TRANSFER) {
            "Spending breakdown is only meaningful for expense or income."
        }

        val groups = categoryRepository.getGroupsWithCategories(spaceId)
            .filter { it.group.type == type }

        val transactions = transactionRepository.getTransactions(spaceId, from, to)
            .filter { it.type == type }

        val totalsById = mutableMapOf<String, Long>()
        val meta = mutableMapOf<String, SliceMeta>()

        groups.forEach { entry ->
            entry.categories.forEach { category ->
                // At the top level everything in a group folds into one slice;
                // drilled in, each category is its own slice.
                val key = when {
                    groupId == null -> entry.group.id
                    entry.group.id == groupId -> category.id
                    else -> return@forEach
                }
                meta.getOrPut(key) {
                    SliceMeta(
                        name = if (groupId == null) entry.group.name else category.name,
                        colorHex = entry.group.color,
                        // Groups carry no icon in the schema, so a group slice
                        // borrows its first category's.
                        iconName = if (groupId == null) {
                            entry.categories.firstOrNull()?.icon ?: category.icon
                        } else {
                            category.icon
                        },
                    )
                }
                val spent = transactions
                    .filter { it.categoryId == category.id }
                    .sumOf { it.amount.cents }
                if (spent > 0) totalsById[key] = (totalsById[key] ?: 0L) + spent
            }
        }

        val totalCents = totalsById.values.sum()
        if (totalCents <= 0L) {
            return SpendingBreakdown(type = type, total = Money.Zero, slices = emptyList())
        }

        val slices = totalsById
            .mapNotNull { (id, cents) ->
                val info = meta[id] ?: return@mapNotNull null
                SpendingSlice(
                    id = id,
                    name = info.name,
                    colorHex = info.colorHex,
                    iconName = info.iconName,
                    total = Money(cents),
                    fraction = cents.toFloat() / totalCents.toFloat(),
                )
            }
            .sortedByDescending { it.total.cents }

        return SpendingBreakdown(type = type, total = Money(totalCents), slices = slices)
    }

    private data class SliceMeta(
        val name: String,
        val colorHex: String,
        val iconName: String,
    )
}
