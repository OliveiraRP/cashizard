package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.BankCategorizationRuleDto
import com.houseofrafa.cashizard.data.dto.BankCategorizationRuleUpsertDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.domain.model.BankCategorizationRule
import com.houseofrafa.cashizard.domain.model.NewBankCategorizationRule
import com.houseofrafa.cashizard.domain.repository.BankCategorizationRuleRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class BankCategorizationRuleRepositoryImpl(
    private val client: SupabaseClient,
) : BankCategorizationRuleRepository {

    override suspend fun getRules(spaceId: String): List<BankCategorizationRule> =
        client.from("bank_categorization_rules").select {
            filter { eq("space_id", spaceId) }
        }.decodeList<BankCategorizationRuleDto>().map { it.toDomain() }

    /**
     * Matching is already case-insensitive at sync time, so the stored form is
     * normalized to lowercase purely so `uq_rule_match` actually catches
     * "Uber" vs "uber" as the same rule rather than two rows.
     */
    override suspend fun upsertRule(command: NewBankCategorizationRule): BankCategorizationRule {
        val matchText = command.matchText.trim().lowercase()
        val dto = BankCategorizationRuleUpsertDto(
            spaceId = command.spaceId,
            matchText = matchText,
            categoryId = command.categoryId,
            walletId = command.walletId,
        )

        return try {
            client.from("bank_categorization_rules").insert(dto) { select() }
                .decodeSingle<BankCategorizationRuleDto>().toDomain()
        } catch (e: Exception) {
            // Row already exists for this (space, matchText) — overwrite its category/wallet.
            client.from("bank_categorization_rules").update({
                set("category_id", command.categoryId)
                set("wallet_id", command.walletId)
            }) {
                select()
                filter {
                    eq("space_id", command.spaceId)
                    eq("match_text", matchText)
                }
            }.decodeSingle<BankCategorizationRuleDto>().toDomain()
        }
    }

    override suspend fun deleteRule(id: String) {
        client.from("bank_categorization_rules").delete {
            filter { eq("id", id) }
        }
    }
}
