package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.domain.model.AuthErrorKind
import com.houseofrafa.cashizard.domain.model.AuthException
import com.houseofrafa.cashizard.domain.model.AuthState
import com.houseofrafa.cashizard.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AuthRepositoryImpl(
    private val client: SupabaseClient,
) : AuthRepository {

    override val authState: Flow<AuthState> =
        client.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated ->
                    AuthState.SignedIn(status.session.user?.id.orEmpty())

                // A failed refresh leaves no usable token, so treat it as signed out
                // and let the user log in again rather than stranding them.
                is SessionStatus.NotAuthenticated,
                is SessionStatus.RefreshFailure,
                -> AuthState.SignedOut

                SessionStatus.Initializing -> AuthState.Unknown
            }
        }

    override suspend fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    override suspend fun signIn(email: String, password: String) = mapErrors {
        client.auth.signInWith(Email) {
            this.email = email.trim()
            this.password = password
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String,
    ): Boolean = mapErrors {
        client.auth.signUpWith(Email) {
            this.email = email.trim()
            this.password = password
            // Read by the schema's handle_new_user() trigger to seed profiles.display_name.
            data = buildJsonObject {
                put("display_name", JsonPrimitive(displayName.trim()))
            }
        }
        // Null when the project has email confirmation enabled.
        client.auth.currentSessionOrNull() != null
    }

    override suspend fun signOut() = mapErrors { client.auth.signOut() }

    override suspend fun sendPasswordReset(email: String) = mapErrors {
        client.auth.resetPasswordForEmail(email.trim())
    }
}

/** Translates Supabase/Ktor failures into domain [AuthException]s. */
private inline fun <T> mapErrors(block: () -> T): T =
    try {
        block()
    } catch (e: AuthRestException) {
        throw AuthException(
            kind = when (e.errorCode) {
                AuthErrorCode.InvalidCredentials -> AuthErrorKind.InvalidCredentials
                AuthErrorCode.EmailNotConfirmed -> AuthErrorKind.EmailNotConfirmed
                AuthErrorCode.EmailExists,
                AuthErrorCode.UserAlreadyExists,
                -> AuthErrorKind.EmailAlreadyRegistered

                AuthErrorCode.WeakPassword -> AuthErrorKind.WeakPassword
                else -> AuthErrorKind.Unknown
            },
            message = e.errorDescription,
            cause = e,
        )
    } catch (e: HttpRequestTimeoutException) {
        throw AuthException(AuthErrorKind.Network, "The request timed out.", e)
    } catch (e: IOException) {
        throw AuthException(AuthErrorKind.Network, "No connection.", e)
    }
