package com.houseofrafa.cashizard.domain.model

data class NewCategoryGroup(
    val spaceId: String,
    val name: String,
    val color: String,
    val type: TransactionType,
    val sortOrder: Int = 0,
)

/**
 * Edits to an existing group. [type] is deliberately absent: categories are
 * already filed under this group and existing transactions reference them, so
 * flipping the type would strand them against the schema's type check.
 */
data class UpdateCategoryGroup(
    val id: String,
    val name: String,
    val color: String,
)

data class NewCategory(
    val groupId: String,
    val name: String,
    val icon: String = "tag",
    val sortOrder: Int = 0,
    val excludeFromAnalytics: Boolean = false,
)

data class UpdateCategory(
    val id: String,
    val groupId: String,
    val name: String,
    val icon: String,
    val excludeFromAnalytics: Boolean,
)
