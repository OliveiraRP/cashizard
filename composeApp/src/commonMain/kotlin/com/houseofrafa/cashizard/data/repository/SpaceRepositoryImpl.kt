package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.SpaceDto
import com.houseofrafa.cashizard.data.dto.SpaceInsertDto
import com.houseofrafa.cashizard.data.dto.SpaceMemberDto
import com.houseofrafa.cashizard.data.dto.SpaceMemberInsertDto
import com.houseofrafa.cashizard.data.dto.SpaceTotalsDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.domain.model.Space
import com.houseofrafa.cashizard.domain.model.SpaceRole
import com.houseofrafa.cashizard.domain.model.SpaceTotals
import com.houseofrafa.cashizard.domain.repository.SpaceRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class SpaceRepositoryImpl(
    private val client: SupabaseClient,
) : SpaceRepository {

    override suspend fun getSpaces(): List<Space> {
        val uid = client.auth.currentUserOrNull()?.id ?: return emptyList()
        val memberships = client.from("space_members").select {
            filter { eq("user_id", uid) }
        }.decodeList<SpaceMemberDto>()
        if (memberships.isEmpty()) return emptyList()

        val roleBySpace = memberships.associate { it.spaceId to it.role }
        val spaces = client.from("spaces").select {
            filter { isIn("id", memberships.map { it.spaceId }) }
        }.decodeList<SpaceDto>()

        return spaces.map { dto ->
            Space(
                id = dto.id,
                name = dto.name,
                role = SpaceRole.fromWire(roleBySpace[dto.id] ?: "member"),
                createdBy = dto.createdBy,
            )
        }
    }

    override suspend fun getSpaceTotals(spaceId: String): SpaceTotals =
        client.from("space_totals").select {
            filter { eq("space_id", spaceId) }
        }.decodeSingle<SpaceTotalsDto>().toDomain()

    override suspend fun createSpace(name: String): Space {
        val uid = client.requireUserId()
        val space = client.from("spaces")
            .insert(SpaceInsertDto(name = name, createdBy = uid)) { select() }
            .decodeSingle<SpaceDto>()
        client.from("space_members")
            .insert(SpaceMemberInsertDto(spaceId = space.id, userId = uid, role = "owner"))
        return Space(id = space.id, name = space.name, role = SpaceRole.OWNER, createdBy = space.createdBy)
    }
}
