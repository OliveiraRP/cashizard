package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.Profile

interface ProfileRepository {
    /** The signed-in user's own profile. */
    suspend fun getProfile(): Profile

    suspend fun updateDisplayName(name: String): Profile
}
