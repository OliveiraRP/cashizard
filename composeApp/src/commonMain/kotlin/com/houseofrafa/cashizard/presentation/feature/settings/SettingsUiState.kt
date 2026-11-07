package com.houseofrafa.cashizard.presentation.feature.settings

/** A bank account link joined with its account's name, for the row label. */
data class BankLinkRow(
    val linkId: String,
    val accountName: String,
    val enableBankingAccountId: String,
)

data class SettingsUiState(
    val displayName: String = "",
    val bankLinks: List<BankLinkRow> = emptyList(),
    val loading: Boolean = true,
    val errorMessage: String? = null,
)
