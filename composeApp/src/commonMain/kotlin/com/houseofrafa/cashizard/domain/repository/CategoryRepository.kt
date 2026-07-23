package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.CategoryGroupWithCategories
import com.houseofrafa.cashizard.domain.model.NewCategory
import com.houseofrafa.cashizard.domain.model.NewCategoryGroup
import com.houseofrafa.cashizard.domain.model.UpdateCategory
import com.houseofrafa.cashizard.domain.model.UpdateCategoryGroup

interface CategoryRepository {
    suspend fun getGroups(spaceId: String): List<CategoryGroup>

    suspend fun getCategories(spaceId: String): List<Category>

    /** Groups with their non-archived categories, ordered for pickers/analytics. */
    suspend fun getGroupsWithCategories(spaceId: String): List<CategoryGroupWithCategories>

    suspend fun createGroup(command: NewCategoryGroup): CategoryGroup

    suspend fun createCategory(command: NewCategory): Category

    suspend fun updateGroup(command: UpdateCategoryGroup): CategoryGroup

    suspend fun updateCategory(command: UpdateCategory): Category

    /**
     * Bulk-writes the analytics-exclusion flag for the given categories
     * (id -> excluded). Callers pass only the categories whose flag changed.
     */
    suspend fun setExcludedFromAnalytics(changes: Map<String, Boolean>)
}
