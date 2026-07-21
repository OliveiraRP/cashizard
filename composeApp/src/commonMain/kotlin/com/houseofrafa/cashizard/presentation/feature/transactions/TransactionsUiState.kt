package com.houseofrafa.cashizard.presentation.feature.transactions

import com.houseofrafa.cashizard.domain.usecase.TransactionDay
import kotlinx.datetime.LocalDate

data class TransactionsUiState(
    /** The first day of the displayed month. */
    val month: LocalDate,
    val days: List<TransactionDay> = emptyList(),
    val totals: MonthTotals = MonthTotals(),
    val loading: Boolean = true,
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean get() = !loading && errorMessage == null && days.isEmpty()
}
