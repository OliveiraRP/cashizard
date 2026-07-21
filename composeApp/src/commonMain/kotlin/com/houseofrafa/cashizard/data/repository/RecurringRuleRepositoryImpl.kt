package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.IdDto
import com.houseofrafa.cashizard.data.dto.RecurringRuleDto
import com.houseofrafa.cashizard.data.dto.RecurringRuleInsertDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.data.mapper.toEuros
import com.houseofrafa.cashizard.domain.model.NewRecurringRule
import com.houseofrafa.cashizard.domain.model.RecurringRule
import com.houseofrafa.cashizard.domain.repository.RecurringRuleRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class RecurringRuleRepositoryImpl(
    private val client: SupabaseClient,
) : RecurringRuleRepository {

    /** Like transactions, rules are scoped to a space through their wallets. */
    override suspend fun getRules(spaceId: String): List<RecurringRule> {
        val walletIds = walletIdsForSpace(spaceId)
        if (walletIds.isEmpty()) return emptyList()

        return client.from("recurring_rules").select {
            filter {
                or {
                    isIn("from_wallet_id", walletIds)
                    isIn("to_wallet_id", walletIds)
                }
            }
            order("next_run", Order.ASCENDING)
        }.decodeList<RecurringRuleDto>().map { it.toDomain() }
    }

    override suspend fun createRule(command: NewRecurringRule): RecurringRule =
        client.from("recurring_rules").insert(
            RecurringRuleInsertDto(
                type = command.type.wire,
                amount = command.amount.toEuros(),
                fromWalletId = command.fromWalletId,
                toWalletId = command.toWalletId,
                categoryId = command.categoryId,
                note = command.note,
                dayOfMonth = command.dayOfMonth,
                nextRun = command.nextRun.toString(),
                createdBy = client.requireUserId(),
            ),
        ) { select() }.decodeSingle<RecurringRuleDto>().toDomain()

    override suspend fun setActive(ruleId: String, active: Boolean) {
        client.from("recurring_rules").update({ set("active", active) }) {
            filter { eq("id", ruleId) }
        }
    }

    override suspend fun deleteRule(ruleId: String) {
        client.from("recurring_rules").delete {
            filter { eq("id", ruleId) }
        }
    }

    private suspend fun walletIdsForSpace(spaceId: String): List<String> =
        client.from("wallets").select(Columns.list("id")) {
            filter { eq("space_id", spaceId) }
        }.decodeList<IdDto>().map { it.id }
}
