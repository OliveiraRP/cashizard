package com.houseofrafa.cashizard.presentation.feature.auth

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val submitting: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = !submitting &&
            name.isNotBlank() &&
            email.isNotBlank() &&
            password.length >= MIN_PASSWORD_LENGTH

    companion object {
        /** Matches the design's "At least 8 characters." hint. */
        const val MIN_PASSWORD_LENGTH = 8
    }
}
