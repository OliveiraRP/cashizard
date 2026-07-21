package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /**
     * Session state over time, including the restore-from-storage phase. The root
     * navigation observes this rather than polling, so a token expiring or a
     * sign-out on another screen drops straight back to the auth stack.
     */
    val authState: Flow<AuthState>

    /** The current authenticated user's id, or null if signed out. */
    suspend fun currentUserId(): String?

    /** @throws AuthException on bad credentials, unconfirmed email, or network failure. */
    suspend fun signIn(email: String, password: String)

    /**
     * [displayName] seeds `profiles.display_name` via the DB's new-user trigger.
     *
     * Returns true when the new account is signed in immediately, false when the
     * project requires email confirmation first — in that case no session exists
     * yet and the caller must tell the user to check their inbox.
     */
    suspend fun signUp(email: String, password: String, displayName: String): Boolean

    suspend fun signOut()

    /** Sends a reset link. Succeeds silently for unknown addresses, by design. */
    suspend fun sendPasswordReset(email: String)
}
