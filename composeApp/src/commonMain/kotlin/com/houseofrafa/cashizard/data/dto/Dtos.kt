package com.houseofrafa.cashizard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================
// Read DTOs (tables & views). Amounts are numeric(12,2) -> Double;
// dates/timestamps are ISO strings parsed in the mappers.
// ============================================================

@Serializable
data class SpaceDto(
    val id: String,
    val name: String,
    @SerialName("created_by") val createdBy: String,
)

@Serializable
data class SpaceMemberDto(
    @SerialName("space_id") val spaceId: String,
    @SerialName("user_id") val userId: String,
    val role: String,
)

@Serializable
data class SpaceTotalsDto(
    @SerialName("space_id") val spaceId: String,
    @SerialName("total_balance") val totalBalance: Double,
    @SerialName("savings_investments_balance") val savingsInvestmentsBalance: Double,
)

@Serializable
data class AccountBalanceDto(
    val id: String,
    @SerialName("space_id") val spaceId: String,
    val name: String,
    val icon: String,
    @SerialName("sort_order") val sortOrder: Int,
    val balance: Double,
)

@Serializable
data class WalletBalanceDto(
    val id: String,
    @SerialName("space_id") val spaceId: String,
    @SerialName("account_id") val accountId: String? = null,
    val name: String,
    val icon: String,
    val type: String,
    @SerialName("goal_amount") val goalAmount: Double? = null,
    @SerialName("annual_budget") val annualBudget: Double? = null,
    @SerialName("sort_order") val sortOrder: Int,
    val archived: Boolean,
    val balance: Double,
    @SerialName("spent_this_year") val spentThisYear: Double? = null,
    @SerialName("annual_budget_left") val annualBudgetLeft: Double? = null,
)

@Serializable
data class CategoryGroupDto(
    val id: String,
    @SerialName("space_id") val spaceId: String,
    val name: String,
    val color: String,
    val type: String,
    @SerialName("sort_order") val sortOrder: Int,
)

@Serializable
data class CategoryDto(
    val id: String,
    @SerialName("group_id") val groupId: String,
    val name: String,
    val icon: String,
    @SerialName("sort_order") val sortOrder: Int,
    val archived: Boolean,
    @SerialName("exclude_from_analytics") val excludeFromAnalytics: Boolean = false,
)

@Serializable
data class TransactionDto(
    val id: String,
    val type: String,
    val amount: Double,
    @SerialName("from_wallet_id") val fromWalletId: String? = null,
    @SerialName("to_wallet_id") val toWalletId: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("occurred_on") val occurredOn: String,
    val note: String? = null,
    @SerialName("recurring_rule_id") val recurringRuleId: String? = null,
    @SerialName("created_by") val createdBy: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class RecurringRuleDto(
    val id: String,
    val type: String,
    val amount: Double,
    @SerialName("from_wallet_id") val fromWalletId: String? = null,
    @SerialName("to_wallet_id") val toWalletId: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    val note: String? = null,
    @SerialName("day_of_month") val dayOfMonth: Int,
    @SerialName("next_run") val nextRun: String,
    val active: Boolean,
    @SerialName("created_by") val createdBy: String,
)

/** Minimal projection for id-only queries. */
@Serializable
data class IdDto(val id: String)

// ============================================================
// Insert DTOs (only writable columns; ids/created_at use DB defaults).
// ============================================================

@Serializable
data class SpaceInsertDto(
    val name: String,
    @SerialName("created_by") val createdBy: String,
)

@Serializable
data class SpaceMemberInsertDto(
    @SerialName("space_id") val spaceId: String,
    @SerialName("user_id") val userId: String,
    val role: String,
)

@Serializable
data class AccountInsertDto(
    @SerialName("space_id") val spaceId: String,
    val name: String,
    val icon: String,
    @SerialName("sort_order") val sortOrder: Int,
)

@Serializable
data class WalletInsertDto(
    @SerialName("space_id") val spaceId: String,
    @SerialName("account_id") val accountId: String? = null,
    val name: String,
    val icon: String,
    val type: String,
    @SerialName("goal_amount") val goalAmount: Double? = null,
    @SerialName("annual_budget") val annualBudget: Double? = null,
    @SerialName("initial_balance") val initialBalance: Double,
    @SerialName("sort_order") val sortOrder: Int,
)

@Serializable
data class CategoryGroupInsertDto(
    @SerialName("space_id") val spaceId: String,
    val name: String,
    val color: String,
    val type: String,
    @SerialName("sort_order") val sortOrder: Int,
)

@Serializable
data class CategoryInsertDto(
    @SerialName("group_id") val groupId: String,
    val name: String,
    val icon: String,
    @SerialName("sort_order") val sortOrder: Int,
    @SerialName("exclude_from_analytics") val excludeFromAnalytics: Boolean = false,
)

@Serializable
data class CategoryGroupUpdateDto(
    val name: String,
    val color: String,
)

@Serializable
data class CategoryUpdateDto(
    @SerialName("group_id") val groupId: String,
    val name: String,
    val icon: String,
    @SerialName("exclude_from_analytics") val excludeFromAnalytics: Boolean,
)

/** Writes only the analytics-exclusion flag, for the bulk filter sheet. */
@Serializable
data class CategoryExclusionUpdateDto(
    @SerialName("exclude_from_analytics") val excludeFromAnalytics: Boolean,
)

@Serializable
data class TransactionInsertDto(
    val type: String,
    val amount: Double,
    @SerialName("from_wallet_id") val fromWalletId: String? = null,
    @SerialName("to_wallet_id") val toWalletId: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("occurred_on") val occurredOn: String,
    val note: String? = null,
    @SerialName("created_by") val createdBy: String,
)

@Serializable
data class RecurringRuleInsertDto(
    val type: String,
    val amount: Double,
    @SerialName("from_wallet_id") val fromWalletId: String? = null,
    @SerialName("to_wallet_id") val toWalletId: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    val note: String? = null,
    @SerialName("day_of_month") val dayOfMonth: Int,
    @SerialName("next_run") val nextRun: String,
    @SerialName("created_by") val createdBy: String,
)
