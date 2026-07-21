package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import kotlinx.serialization.Serializable

/** Which wallet field a wallet pick applies to. */
@Serializable
enum class WalletTarget { From, To }

/**
 * The entries on the add-transaction sheet's internal push stack.
 *
 * The forms carry no id: which group or category is being edited lives in
 * [AddTransactionUiState.groupForm] / [AddTransactionUiState.categoryForm], so
 * the pushed pickers can write into the same form without passing it along.
 */
@Serializable
sealed interface AddTransactionSheetConfig {
    @Serializable
    data object NewTransaction : AddTransactionSheetConfig

    @Serializable
    data object CategoryPicker : AddTransactionSheetConfig

    @Serializable
    data class WalletPicker(val target: WalletTarget) : AddTransactionSheetConfig

    @Serializable
    data object GroupForm : AddTransactionSheetConfig

    @Serializable
    data object CategoryForm : AddTransactionSheetConfig

    @Serializable
    data object GroupPicker : AddTransactionSheetConfig

    @Serializable
    data object IconPicker : AddTransactionSheetConfig

    @Serializable
    data object EditCategories : AddTransactionSheetConfig
}
