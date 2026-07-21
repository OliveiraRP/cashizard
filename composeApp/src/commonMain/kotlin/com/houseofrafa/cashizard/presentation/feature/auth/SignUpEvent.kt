package com.houseofrafa.cashizard.presentation.feature.auth

/** One-shot outcomes of signing up that the UI, not the ViewModel, acts on. */
sealed interface SignUpEvent {
    /**
     * The account was created but the project requires email confirmation, so no
     * session exists and nothing will navigate on its own. The UI returns to log
     * in and points the user at their inbox.
     */
    data object ConfirmationRequired : SignUpEvent
}
