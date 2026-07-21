package com.houseofrafa.cashizard.presentation.feature.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val submitting: Boolean = false,
    val errorMessage: String? = null,
    /** Confirmation shown after a reset email is requested. */
    val infoMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = !submitting && email.isNotBlank() && password.isNotBlank()
}
