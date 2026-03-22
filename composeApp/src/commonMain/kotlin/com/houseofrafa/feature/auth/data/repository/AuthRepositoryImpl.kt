package com.houseofrafa.feature.auth.data.repository

import com.houseofrafa.core.data.local.UserPreferences
import com.houseofrafa.core.data.remote.supabase
import com.houseofrafa.core.domain.model.User
import com.houseofrafa.feature.auth.data.dto.UserDto
import com.houseofrafa.feature.auth.data.dto.toDomain
import com.houseofrafa.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl(
    private val userPreferences: UserPreferences,
) : AuthRepository {

    override suspend fun loginWithEmail(email: String, password: String): Result<User> = runCatching {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        val user = supabase.postgrest
            .from("users")
            .select()
            .decodeSingle<UserDto>()
            .toDomain()
        userPreferences.saveUser(user)
        user
    }.onFailure { e ->
        println("AUTH ERROR: ${e::class.simpleName}: ${e.message}")
    }

    override suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User> = runCatching {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject { put("name", name) }
        }
        supabase.auth.currentUserOrNull() ?: error("Sign up failed")
        val userDto = supabase.postgrest
            .from("users")
            .select()
            .decodeSingle<UserDto>()
        val user = userDto.toDomain()
        userPreferences.saveUser(user)
        user
    }.onFailure { e ->
        println("SIGNUP ERROR: ${e::class.simpleName}: ${e.message}")
    }

    override suspend fun validateSession(): Result<Boolean> = runCatching {
        supabase.auth.sessionStatus
            .first { it !is SessionStatus.Initializing }
            .let { it is SessionStatus.Authenticated }
    }.onFailure { e ->
        println("SESSION ERROR: ${e::class.simpleName}: ${e.message}")
    }
}
