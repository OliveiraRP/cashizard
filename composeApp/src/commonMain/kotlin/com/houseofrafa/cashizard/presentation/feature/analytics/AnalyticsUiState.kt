package com.houseofrafa.cashizard.presentation.feature.analytics

import com.houseofrafa.cashizard.domain.usecase.SpendingBreakdown
import kotlinx.datetime.LocalDate

data class AnalyticsUiState(
    val month: LocalDate,
    val breakdown: SpendingBreakdown? = null,
    /** Set while drilled into one group; null at the top level. */
    val drilledGroupId: String? = null,
    val drilledGroupName: String? = null,
    val loading: Boolean = true,
    val errorMessage: String? = null,
) {
    val isDrilled: Boolean get() = drilledGroupId != null

    val isEmpty: Boolean
        get() = !loading && errorMessage == null && breakdown?.slices.isNullOrEmpty()
}
