package com.houseofrafa.feature.auth.data.repository

import com.houseofrafa.core.data.local.UserPreferences
import com.houseofrafa.core.data.remote.supabase
import com.houseofrafa.core.domain.model.User
import com.houseofrafa.feature.auth.data.dto.UserDto
import com.houseofrafa.feature.auth.data.dto.toDomain
import com.houseofrafa.feature.auth.domain.repository.AuthRepository
import io.github.jan.supabase.postgrest.postgrest

class AuthRepositoryImpl(
    private val userPreferences: UserPreferences,
) : AuthRepository {

    override suspend fun loginWithToken(token: String): Result<User> = runCatching {
        val matches = supabase.postgrest
            .from("users")
            .select {
                filter { eq("token", token) }
                limit(1)
            }
            .decodeList<UserDto>()

        val userDto = matches.firstOrNull()
            ?: error("Token not found")

        val user = userDto.toDomain()
        userPreferences.saveUser(user)
        user
    }.onFailure { e ->
        println("AUTH ERROR: ${e::class.simpleName}: ${e.message}")
    }

    override suspend fun validateStoredToken(): Result<User?> = runCatching {
        val savedUser = userPreferences.getUser() ?: return@runCatching null

        val matches = supabase.postgrest
            .from("users")
            .select {
                filter { eq("token", savedUser.token) }
                limit(1)
            }
            .decodeList<UserDto>()

        if (matches.isEmpty()) {
            userPreferences.clearUser()
            null
        } else {
            matches.first().toDomain()
        }
    }.onFailure { e ->
        println("VALIDATE TOKEN ERROR: ${e::class.simpleName}: ${e.message}")
    }

    override fun getSavedUser(): User? = userPreferences.getUser()

    override fun clearUser() = userPreferences.clearUser()
}
