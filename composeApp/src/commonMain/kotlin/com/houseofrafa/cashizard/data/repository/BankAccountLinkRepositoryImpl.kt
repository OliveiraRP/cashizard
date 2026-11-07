package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.BankAccountLinkDto
import com.houseofrafa.cashizard.data.dto.BankAccountLinkInsertDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.domain.model.BankAccountLink
import com.houseofrafa.cashizard.domain.model.NewBankAccountLink
import com.houseofrafa.cashizard.domain.repository.BankAccountLinkRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class BankAccountLinkRepositoryImpl(
    private val client: SupabaseClient,
) : BankAccountLinkRepository {

    override suspend fun getLinks(spaceId: String): List<BankAccountLink> =
        client.from("bank_account_links").select {
            filter { eq("space_id", spaceId) }
        }.decodeList<BankAccountLinkDto>().map { it.toDomain() }

    override suspend fun createLink(command: NewBankAccountLink): BankAccountLink =
        client.from("bank_account_links").insert(
            BankAccountLinkInsertDto(
                spaceId = command.spaceId,
                accountId = command.accountId,
                enableBankingAccountId = command.enableBankingAccountId,
            ),
        ) { select() }.decodeSingle<BankAccountLinkDto>().toDomain()

    override suspend fun deleteLink(id: String) {
        client.from("bank_account_links").delete {
            filter { eq("id", id) }
        }
    }
}
