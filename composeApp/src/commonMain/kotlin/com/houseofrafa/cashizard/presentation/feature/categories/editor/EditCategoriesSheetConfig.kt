package com.houseofrafa.cashizard.presentation.feature.categories.editor

import kotlinx.serialization.Serializable

/**
 * The standalone Edit-Categories sheet's internal push stack, opened from
 * Settings. The forms carry no id: which group or category is being edited
 * lives in [EditCategoriesUiState.groupForm] / [EditCategoriesUiState.categoryForm].
 */
@Serializable
sealed interface EditCategoriesSheetConfig {
    @Serializable
    data object Root : EditCategoriesSheetConfig

    @Serializable
    data object GroupForm : EditCategoriesSheetConfig

    @Serializable
    data object CategoryForm : EditCategoriesSheetConfig

    @Serializable
    data object GroupPicker : EditCategoriesSheetConfig

    @Serializable
    data object IconPicker : EditCategoriesSheetConfig
}
