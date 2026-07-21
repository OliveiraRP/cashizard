package com.houseofrafa.cashizard.presentation.feature.wallets.addaccount

data class AddAccountUiState(
    val name: String = "",
    val saving: Boolean = false,
    val errorMessage: String? = null,
    /**
     * The sheet has asked to close. The host plays the dismiss animation and
     * then reports back, so closing stays one-way: ViewModel to UI.
     */
    val closeRequested: Boolean = false,
) {
    val canSave: Boolean get() = !saving && name.isNotBlank()
}
