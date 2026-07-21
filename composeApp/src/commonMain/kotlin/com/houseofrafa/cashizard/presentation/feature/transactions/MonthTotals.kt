package com.houseofrafa.cashizard.presentation.feature.transactions

import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.usecase.TransactionDay

/**
 * The month's income, expenses and their difference. Transfers are excluded:
 * moving money between wallets is neither earning nor spending it.
 */
data class MonthTotals(
    val income: Money = Money.Zero,
    val expenses: Money = Money.Zero,
) {
    val net: Money get() = income - expenses
}

internal fun List<TransactionDay>.monthTotals(): MonthTotals {
    var income = 0L
    var expenses = 0L
    forEach { day ->
        day.transactions.forEach { details ->
            when (details.transaction.type) {
                TransactionType.INCOME -> income += details.transaction.amount.cents
                TransactionType.EXPENSE -> expenses += details.transaction.amount.cents
                TransactionType.TRANSFER -> Unit
            }
        }
    }
    return MonthTotals(income = Money(income), expenses = Money(expenses))
}
