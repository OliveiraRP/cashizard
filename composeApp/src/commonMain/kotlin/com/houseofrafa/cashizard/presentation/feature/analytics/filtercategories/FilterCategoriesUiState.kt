package com.houseofrafa.cashizard.presentation.feature.analytics.filtercategories

import com.houseofrafa.cashizard.domain.model.CategoryGroupWithCategories

/**
 * The "Filter categories" sheet: every group and its categories, each category
 * carrying a checkmark. A checked category counts in analytics; an unchecked one
 * is excluded. [checkedIds] is the live selection; [groups] still holds each
 * category's saved flag, so [changes] can persist only what actually moved.
 */
data class FilterCategoriesUiState(
    val groups: List<CategoryGroupWithCategories> = emptyList(),
    val checkedIds: Set<String> = emptySet(),
    val loading: Boolean = true,
    val saving: Boolean = false,
    val errorMessage: String? = null,
    val closeRequested: Boolean = false,
) {
    fun isChecked(categoryId: String): Boolean = categoryId in checkedIds

    val canSave: Boolean get() = !loading && !saving

    /** Categories whose exclusion flag differs from what was loaded (id -> excluded). */
    val changes: Map<String, Boolean>
        get() = groups
            .flatMap { it.categories }
            .mapNotNull { category ->
                val nowExcluded = category.id !in checkedIds
                (category.id to nowExcluded).takeIf { nowExcluded != category.excludeFromAnalytics }
            }
            .toMap()
}
