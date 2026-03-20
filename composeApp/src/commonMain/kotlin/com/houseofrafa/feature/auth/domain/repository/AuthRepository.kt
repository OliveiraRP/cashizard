package com.houseofrafa.feature.auth.domain.repository

import com.houseofrafa.core.domain.model.User

interface AuthRepository {
    suspend fun loginWithToken(token: String): Result<User>
    suspend fun validateStoredToken(): Result<User?>
    fun getSavedUser(): User?
    fun clearUser()
}
