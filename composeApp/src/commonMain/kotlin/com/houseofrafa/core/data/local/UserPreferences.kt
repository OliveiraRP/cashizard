package com.houseofrafa.core.data.local

import com.houseofrafa.core.domain.model.User
import com.russhwolf.settings.Settings

class UserPreferences {

    private val settings: Settings = Settings()

    fun saveUser(user: User) {
        settings.putInt(KEY_USER_ID, user.userId)
        settings.putString(KEY_NAME, user.name)
        settings.putString(KEY_TOKEN, user.token)
    }

    fun getUser(): User? {
        val userId = settings.getIntOrNull(KEY_USER_ID)  ?: return null
        val name  = settings.getStringOrNull(KEY_NAME)   ?: return null
        val token = settings.getStringOrNull(KEY_TOKEN)  ?: return null
        return User(userId = userId, name = name, token = token)
    }

    fun clearUser() {
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_NAME)
        settings.remove(KEY_TOKEN)
    }

    private companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_NAME    = "name"
        const val KEY_TOKEN   = "token"
    }
}
