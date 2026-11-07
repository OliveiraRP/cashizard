package com.houseofrafa.cashizard.presentation.feature.categories.editor

import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.CategoryGroupWithCategories
import com.houseofrafa.cashizard.presentation.feature.categories.CategoryFormUiState
import com.houseofrafa.cashizard.presentation.feature.categories.GroupFormUiState

data class EditCategoriesUiState(
    val categoryGroups: List<CategoryGroupWithCategories> = emptyList(),
    /** Non-null only while a group form is on the stack. */
    val groupForm: GroupFormUiState? = null,
    /** Non-null only while a category form is on the stack. */
    val categoryForm: CategoryFormUiState? = null,
    val loading: Boolean = true,
    val errorMessage: String? = null,
    /** The sheet has asked to close; the host plays the dismiss animation. */
    val closeRequested: Boolean = false,
) {
    val groups: List<CategoryGroup> get() = categoryGroups.map { it.group }

    fun group(id: String?): CategoryGroup? = groups.firstOrNull { it.id == id }

    fun category(id: String?): Category? = categoryGroups.flatMap { it.categories }.firstOrNull { it.id == id }

    /** The group a category is filed under, for prefilling the edit form. */
    fun groupOf(categoryId: String): CategoryGroup? =
        categoryGroups.firstOrNull { entry -> entry.categories.any { it.id == categoryId } }?.group
}
