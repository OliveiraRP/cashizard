package com.houseofrafa.cashizard.domain.model

/**
 * A category group scoped to a [type]. Only categories whose group type matches a
 * transaction's type are selectable for it. [color] is a stored `#RRGGBB` hex.
 */
data class CategoryGroup(
    val id: String,
    val spaceId: String,
    val name: String,
    val color: String,
    val type: TransactionType,
    val sortOrder: Int,
)

/** A category within a group. [icon] is a DB icon-name string. */
data class Category(
    val id: String,
    val groupId: String,
    val name: String,
    val icon: String,
    val sortOrder: Int,
    val archived: Boolean,
)

/** A group together with its (non-archived) categories, for pickers/analytics. */
data class CategoryGroupWithCategories(
    val group: CategoryGroup,
    val categories: List<Category>,
)
