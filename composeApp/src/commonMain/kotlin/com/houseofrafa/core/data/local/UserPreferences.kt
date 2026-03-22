package com.houseofrafa.core.data.local

import com.houseofrafa.core.domain.model.User
import com.russhwolf.settings.Settings

class UserPreferences {

    private val settings: Settings = Settings()

    fun saveUser(user: User) {
        settings.putString(KEY_USER_ID, user.userId)
        settings.putString(KEY_NAME, user.name)
    }

    private companion object {
        const val KEY_USER_ID = "user_id"
        const val KEY_NAME    = "name"
    }
}
