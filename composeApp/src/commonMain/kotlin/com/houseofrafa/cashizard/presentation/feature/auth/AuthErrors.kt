package com.houseofrafa.cashizard.presentation.feature.auth

import com.houseofrafa.cashizard.domain.model.AuthErrorKind
import com.houseofrafa.cashizard.domain.model.AuthException

/** Server error text is not user-facing copy, so map the known cases to our own. */
internal fun AuthException.userMessage(): String = when (kind) {
    AuthErrorKind.InvalidCredentials -> "Wrong email or password. Try again."
    AuthErrorKind.EmailNotConfirmed -> "Confirm your email address first — check your inbox."
    AuthErrorKind.EmailAlreadyRegistered -> "That email already has an account. Log in instead."
    AuthErrorKind.WeakPassword -> "Pick a stronger password — at least 8 characters."
    AuthErrorKind.Network -> "No connection. Check your network and try again."
    AuthErrorKind.Unknown -> "Something went wrong. Please try again."
}
