package com.houseofrafa.cashizard.presentation.feature.settings.rules

import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.CategoryGroupWithCategories
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.model.Wallet

/** One rule, with the category/wallet names already resolved for display. */
data class RuleRow(
    val ruleId: String,
    val matchText: String,
    val categoryId: String,
    val categoryName: String,
    val walletId: String,
    val walletName: String,
)

data class EditRulesUiState(
    val rules: List<RuleRow> = emptyList(),
    val categoryGroups: List<CategoryGroupWithCategories> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    /** Non-null only while the rule form is on the stack. */
    val ruleForm: RuleFormUiState? = null,
    val loading: Boolean = true,
    val errorMessage: String? = null,
    /** The sheet has asked to close; the host plays the dismiss animation. */
    val closeRequested: Boolean = false,
) {
    fun categoriesForType(type: TransactionType): List<Category> =
        categoryGroups.filter { it.group.type == type }.flatMap { it.categories }

    fun category(id: String?): Category? = categoryGroups.flatMap { it.categories }.firstOrNull { it.id == id }

    fun groupOf(categoryId: String): CategoryGroup? =
        categoryGroups.firstOrNull { entry -> entry.categories.any { it.id == categoryId } }?.group

    fun wallet(id: String?): Wallet? = wallets.firstOrNull { it.id == id }
}
