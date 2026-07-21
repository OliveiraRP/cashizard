package com.houseofrafa.cashizard.presentation.feature.categories

import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.TransactionType

/**
 * The "New / Edit Category" form. A category has no color of its own — it wears
 * its group's — so the form only covers name, group and icon.
 *
 * [restrictToType] is the transaction type whose groups may be chosen. Moving a
 * category to a group of another type would strand its existing transactions
 * against the schema's type check, so the group picker never offers them.
 */
data class CategoryFormUiState(
    val categoryId: String? = null,
    val groupId: String? = null,
    val name: String = "",
    val icon: String = "tag",
    val restrictToType: TransactionType = TransactionType.EXPENSE,
    val saving: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEditing: Boolean get() = categoryId != null

    val canSave: Boolean get() = !saving && name.isNotBlank() && groupId != null

    companion object {
        fun editing(category: Category, group: CategoryGroup): CategoryFormUiState =
            CategoryFormUiState(
                categoryId = category.id,
                groupId = category.groupId,
                name = category.name,
                icon = category.icon,
                restrictToType = group.type,
            )

        /** A new category defaults into the first group of the active type. */
        fun creating(type: TransactionType, groups: List<CategoryGroup>): CategoryFormUiState =
            CategoryFormUiState(
                groupId = groups.firstOrNull { it.type == type }?.id,
                icon = if (type == TransactionType.TRANSFER) "arrow-right-left" else "tag",
                restrictToType = type,
            )
    }
}
