package com.houseofrafa.cashizard.presentation.feature.settings.editprofile

data class EditProfileUiState(
    val name: String = "",
    val loading: Boolean = true,
    val saving: Boolean = false,
    val errorMessage: String? = null,
    /** The sheet has asked to close; the host plays the dismiss animation. */
    val closeRequested: Boolean = false,
) {
    val canSave: Boolean get() = !loading && !saving && name.isNotBlank()
}
