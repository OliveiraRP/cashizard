package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.Space
import com.houseofrafa.cashizard.domain.model.SpaceTotals

interface SpaceRepository {
    /** Spaces the current user is a member of, with their role. */
    suspend fun getSpaces(): List<Space>

    /** Aggregated totals for a space (from the space_totals view). */
    suspend fun getSpaceTotals(spaceId: String): SpaceTotals

    /** Create a space and add the current user as its owner. */
    suspend fun createSpace(name: String): Space
}
