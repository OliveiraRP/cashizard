package com.houseofrafa.cashizard.domain.usecase

import com.houseofrafa.cashizard.domain.model.CategoryGroupWithCategories
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.repository.CategoryRepository

/**
 * Category groups selectable for a transaction of [type]. The schema rejects a
 * category whose group type differs from the transaction type, so the picker only
 * ever shows matching groups. Empty groups are dropped.
 */
class GetSelectableCategories(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(
        spaceId: String,
        type: TransactionType,
    ): List<CategoryGroupWithCategories> =
        categoryRepository.getGroupsWithCategories(spaceId)
            .filter { it.group.type == type && it.categories.isNotEmpty() }
}
