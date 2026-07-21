package com.houseofrafa.cashizard.domain.model

/**
 * Whether the app currently holds a usable session. [Unknown] is the startup
 * state while persisted credentials are being restored — the UI shows a splash
 * for it rather than flashing the login screen.
 */
sealed interface AuthState {
    data object Unknown : AuthState
    data object SignedOut : AuthState
    data class SignedIn(val userId: String) : AuthState
}

/**
 * Auth failures worth reacting to differently in the UI. Anything else surfaces
 * as [Unknown] with the underlying message.
 */
enum class AuthErrorKind {
    InvalidCredentials,
    EmailNotConfirmed,
    EmailAlreadyRegistered,
    WeakPassword,
    Network,
    Unknown,
}

/** Thrown by the auth repository so the presentation layer never sees Supabase types. */
class AuthException(
    val kind: AuthErrorKind,
    override val message: String,
    override val cause: Throwable? = null,
) : Exception(message, cause)
