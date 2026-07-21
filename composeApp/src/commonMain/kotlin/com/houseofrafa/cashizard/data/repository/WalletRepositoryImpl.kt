package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.IdDto
import com.houseofrafa.cashizard.data.dto.WalletBalanceDto
import com.houseofrafa.cashizard.data.dto.WalletInsertDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.data.mapper.toEuros
import com.houseofrafa.cashizard.domain.model.NewWallet
import com.houseofrafa.cashizard.domain.model.UpdateWallet
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.repository.WalletRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class WalletRepositoryImpl(
    private val client: SupabaseClient,
) : WalletRepository {

    override suspend fun getWallets(spaceId: String, includeArchived: Boolean): List<Wallet> =
        client.from("wallet_balances").select {
            filter {
                eq("space_id", spaceId)
                if (!includeArchived) eq("archived", false)
            }
            order("sort_order", Order.ASCENDING)
        }.decodeList<WalletBalanceDto>().map { it.toDomain() }

    override suspend fun createWallet(command: NewWallet): Wallet {
        val inserted = client.from("wallets").insert(
            WalletInsertDto(
                spaceId = command.spaceId,
                accountId = command.accountId,
                name = command.name,
                icon = command.icon,
                type = command.type.wire,
                goalAmount = command.goalAmount?.toEuros(),
                annualBudget = command.annualBudget?.toEuros(),
                initialBalance = command.initialBalance.toEuros(),
                sortOrder = command.sortOrder,
            ),
        ) { select() }.decodeSingle<IdDto>()

        return client.from("wallet_balances").select {
            filter { eq("id", inserted.id) }
        }.decodeSingle<WalletBalanceDto>().toDomain()
    }

    override suspend fun updateWallet(command: UpdateWallet): Wallet {
        // The `set` builder is used rather than a DTO so the unused amount
        // column is explicitly written to null; the schema's chk_type_fields
        // rejects a goal wallet that still carries an annual_budget, and vice
        // versa. initial_balance is intentionally left untouched.
        client.from("wallets").update({
            set("account_id", command.accountId)
            set("name", command.name)
            set("icon", command.icon)
            set("type", command.type.wire)
            set("goal_amount", command.goalAmount?.toEuros())
            set("annual_budget", command.annualBudget?.toEuros())
        }) {
            filter { eq("id", command.id) }
        }

        return client.from("wallet_balances").select {
            filter { eq("id", command.id) }
        }.decodeSingle<WalletBalanceDto>().toDomain()
    }

    override suspend fun setArchived(walletId: String, archived: Boolean) {
        client.from("wallets").update({ set("archived", archived) }) {
            filter { eq("id", walletId) }
        }
    }
}
