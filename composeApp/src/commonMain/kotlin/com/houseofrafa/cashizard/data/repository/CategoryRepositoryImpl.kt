package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.CategoryDto
import com.houseofrafa.cashizard.data.dto.CategoryGroupDto
import com.houseofrafa.cashizard.data.dto.CategoryGroupInsertDto
import com.houseofrafa.cashizard.data.dto.CategoryGroupUpdateDto
import com.houseofrafa.cashizard.data.dto.CategoryInsertDto
import com.houseofrafa.cashizard.data.dto.CategoryUpdateDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.CategoryGroupWithCategories
import com.houseofrafa.cashizard.domain.model.NewCategory
import com.houseofrafa.cashizard.domain.model.NewCategoryGroup
import com.houseofrafa.cashizard.domain.model.UpdateCategory
import com.houseofrafa.cashizard.domain.model.UpdateCategoryGroup
import com.houseofrafa.cashizard.domain.repository.CategoryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class CategoryRepositoryImpl(
    private val client: SupabaseClient,
) : CategoryRepository {

    override suspend fun getGroups(spaceId: String): List<CategoryGroup> =
        fetchGroups(spaceId).map { it.toDomain() }

    /** `categories` has no space_id — it is scoped through its group. */
    override suspend fun getCategories(spaceId: String): List<Category> {
        val groupIds = fetchGroups(spaceId).map { it.id }
        return fetchCategories(groupIds).map { it.toDomain() }
    }

    override suspend fun getGroupsWithCategories(spaceId: String): List<CategoryGroupWithCategories> {
        val groups = fetchGroups(spaceId)
        if (groups.isEmpty()) return emptyList()

        val categoriesByGroup = fetchCategories(groups.map { it.id })
            .filterNot { it.archived }
            .groupBy { it.groupId }

        return groups.map { group ->
            CategoryGroupWithCategories(
                group = group.toDomain(),
                categories = categoriesByGroup[group.id].orEmpty().map { it.toDomain() },
            )
        }
    }

    override suspend fun createGroup(command: NewCategoryGroup): CategoryGroup =
        client.from("category_groups").insert(
            CategoryGroupInsertDto(
                spaceId = command.spaceId,
                name = command.name,
                color = command.color,
                type = command.type.wire,
                sortOrder = command.sortOrder,
            ),
        ) { select() }.decodeSingle<CategoryGroupDto>().toDomain()

    override suspend fun createCategory(command: NewCategory): Category =
        client.from("categories").insert(
            CategoryInsertDto(
                groupId = command.groupId,
                name = command.name,
                icon = command.icon,
                sortOrder = command.sortOrder,
            ),
        ) { select() }.decodeSingle<CategoryDto>().toDomain()

    override suspend fun updateGroup(command: UpdateCategoryGroup): CategoryGroup =
        client.from("category_groups").update(
            CategoryGroupUpdateDto(name = command.name, color = command.color),
        ) {
            select()
            filter { eq("id", command.id) }
        }.decodeSingle<CategoryGroupDto>().toDomain()

    override suspend fun updateCategory(command: UpdateCategory): Category =
        client.from("categories").update(
            CategoryUpdateDto(
                groupId = command.groupId,
                name = command.name,
                icon = command.icon,
            ),
        ) {
            select()
            filter { eq("id", command.id) }
        }.decodeSingle<CategoryDto>().toDomain()

    private suspend fun fetchGroups(spaceId: String): List<CategoryGroupDto> =
        client.from("category_groups").select {
            filter { eq("space_id", spaceId) }
            order("sort_order", Order.ASCENDING)
        }.decodeList<CategoryGroupDto>()

    private suspend fun fetchCategories(groupIds: List<String>): List<CategoryDto> {
        if (groupIds.isEmpty()) return emptyList()
        return client.from("categories").select {
            filter { isIn("group_id", groupIds) }
            order("sort_order", Order.ASCENDING)
        }.decodeList<CategoryDto>()
    }
}
