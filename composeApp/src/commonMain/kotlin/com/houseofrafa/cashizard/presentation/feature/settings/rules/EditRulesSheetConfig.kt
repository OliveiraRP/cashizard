package com.houseofrafa.cashizard.presentation.feature.settings.rules

import kotlinx.serialization.Serializable

@Serializable
sealed interface EditRulesSheetConfig {
    @Serializable
    data object Root : EditRulesSheetConfig

    @Serializable
    data object RuleForm : EditRulesSheetConfig

    @Serializable
    data object CategoryPicker : EditRulesSheetConfig

    @Serializable
    data object WalletPicker : EditRulesSheetConfig
}
