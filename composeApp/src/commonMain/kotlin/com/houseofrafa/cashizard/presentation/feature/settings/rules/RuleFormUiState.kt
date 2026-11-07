package com.houseofrafa.cashizard.presentation.feature.settings.rules

import com.houseofrafa.cashizard.domain.model.TransactionType

/** The "New / Edit Rule" form. A null [ruleId] means the form is creating. */
data class RuleFormUiState(
    val ruleId: String? = null,
    val matchText: String = "",
    /** Rules only ever apply to expense or income bank transactions. */
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: String? = null,
    val walletId: String? = null,
    val saving: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEditing: Boolean get() = ruleId != null
    val canSave: Boolean get() = !saving && matchText.isNotBlank() && categoryId != null && walletId != null
}
