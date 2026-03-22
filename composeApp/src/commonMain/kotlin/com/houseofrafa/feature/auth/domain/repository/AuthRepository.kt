package com.houseofrafa.feature.auth.domain.repository

import com.houseofrafa.core.domain.model.User

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User>
    suspend fun validateSession(): Result<Boolean>
}
