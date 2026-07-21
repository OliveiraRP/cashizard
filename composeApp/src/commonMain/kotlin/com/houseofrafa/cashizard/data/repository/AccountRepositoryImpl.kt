package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.AccountBalanceDto
import com.houseofrafa.cashizard.data.dto.AccountInsertDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.NewAccount
import com.houseofrafa.cashizard.domain.model.UpdateAccount
import com.houseofrafa.cashizard.domain.repository.AccountRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class AccountRepositoryImpl(
    private val client: SupabaseClient,
) : AccountRepository {

    override suspend fun getAccounts(spaceId: String): List<Account> =
        client.from("account_balances").select {
            filter { eq("space_id", spaceId) }
            order("sort_order", Order.ASCENDING)
        }.decodeList<AccountBalanceDto>().map { it.toDomain() }

    override suspend fun createAccount(command: NewAccount): Account {
        // Insert into the table, then read back the row from the balances view.
        val inserted = client.from("accounts").insert(
            AccountInsertDto(
                spaceId = command.spaceId,
                name = command.name,
                icon = command.icon,
                sortOrder = command.sortOrder,
            ),
        ) { select() }.decodeSingle<com.houseofrafa.cashizard.data.dto.IdDto>()

        return client.from("account_balances").select {
            filter { eq("id", inserted.id) }
        }.decodeSingle<AccountBalanceDto>().toDomain()
    }

    override suspend fun updateAccount(command: UpdateAccount): Account {
        client.from("accounts").update({ set("name", command.name) }) {
            filter { eq("id", command.id) }
        }

        return client.from("account_balances").select {
            filter { eq("id", command.id) }
        }.decodeSingle<AccountBalanceDto>().toDomain()
    }

    /** The `on delete set null` FK leaves this account's wallets standalone. */
    override suspend fun deleteAccount(accountId: String) {
        client.from("accounts").delete {
            filter { eq("id", accountId) }
        }
    }
}
