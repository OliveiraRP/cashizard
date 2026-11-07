package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.ProfileDto
import com.houseofrafa.cashizard.data.dto.ProfileUpdateDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.domain.model.Profile
import com.houseofrafa.cashizard.domain.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class ProfileRepositoryImpl(
    private val client: SupabaseClient,
) : ProfileRepository {

    override suspend fun getProfile(): Profile =
        client.from("profiles").select {
            filter { eq("id", client.requireUserId()) }
        }.decodeSingle<ProfileDto>().toDomain()

    override suspend fun updateDisplayName(name: String): Profile {
        val userId = client.requireUserId()
        client.from("profiles").update(ProfileUpdateDto(displayName = name)) {
            filter { eq("id", userId) }
        }

        return client.from("profiles").select {
            filter { eq("id", userId) }
        }.decodeSingle<ProfileDto>().toDomain()
    }
}
