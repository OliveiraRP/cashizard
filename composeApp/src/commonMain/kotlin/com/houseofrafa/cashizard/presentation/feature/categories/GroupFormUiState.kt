package com.houseofrafa.cashizard.presentation.feature.categories

import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors

/**
 * The "New / Edit Category Group" form. A null [groupId] means the form is
 * creating; otherwise it is editing that group.
 */
data class GroupFormUiState(
    val groupId: String? = null,
    val name: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val color: String = CategoryColors.defaultHexFor(TransactionType.EXPENSE),
    val saving: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEditing: Boolean get() = groupId != null

    /**
     * Transfer groups are always gray, so the form hides the palette entirely
     * rather than offering a single swatch that cannot be changed.
     */
    val picksColor: Boolean get() = CategoryColors.isPickable(type)

    /**
     * The type is fixed once a group exists: its categories are already
     * referenced by transactions that the schema checks against this type.
     */
    val picksType: Boolean get() = !isEditing

    val canSave: Boolean get() = !saving && name.isNotBlank()

    companion object {
        fun editing(group: CategoryGroup): GroupFormUiState = GroupFormUiState(
            groupId = group.id,
            name = group.name,
            type = group.type,
            color = group.color,
        )
    }
}
