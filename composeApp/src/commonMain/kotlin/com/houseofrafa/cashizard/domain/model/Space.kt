package com.houseofrafa.cashizard.domain.model

/** A budget space the current user belongs to. [role] is the current user's role. */
data class Space(
    val id: String,
    val name: String,
    val role: SpaceRole,
    val createdBy: String,
)

/** Aggregated space figures from the `space_totals` view. */
data class SpaceTotals(
    val spaceId: String,
    val totalBalance: Money,
    val savingsInvestmentsBalance: Money,
)
