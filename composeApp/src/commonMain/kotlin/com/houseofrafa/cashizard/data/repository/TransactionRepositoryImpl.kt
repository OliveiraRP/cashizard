package com.houseofrafa.cashizard.data.repository

import com.houseofrafa.cashizard.data.dto.IdDto
import com.houseofrafa.cashizard.data.dto.TransactionDto
import com.houseofrafa.cashizard.data.dto.TransactionInsertDto
import com.houseofrafa.cashizard.data.mapper.toDomain
import com.houseofrafa.cashizard.data.mapper.toEuros
import com.houseofrafa.cashizard.domain.model.NewTransaction
import com.houseofrafa.cashizard.domain.model.Transaction
import com.houseofrafa.cashizard.domain.model.UpdateTransaction
import com.houseofrafa.cashizard.domain.repository.TransactionRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.LocalDate

class TransactionRepositoryImpl(
    private val client: SupabaseClient,
) : TransactionRepository {

    /**
     * `transactions` has no space_id: a transaction belongs to a space through its
     * wallets, and transfers may cross spaces. So we resolve the space's wallet ids
     * first and match either side.
     */
    override suspend fun getTransactions(
        spaceId: String,
        from: LocalDate,
        to: LocalDate,
    ): List<Transaction> {
        val walletIds = walletIdsForSpace(spaceId)
        if (walletIds.isEmpty()) return emptyList()

        return client.from("transactions").select {
            filter {
                // Both bounds must live inside one `and` group: the builder keys
                // params by column and only the first value per key reaches the
                // URL, so a bare gte + lte on occurred_on silently drops the lte
                // and every later month leaks into the results.
                and {
                    gte("occurred_on", from.toString())
                    lte("occurred_on", to.toString())
                }
                or {
                    isIn("from_wallet_id", walletIds)
                    isIn("to_wallet_id", walletIds)
                }
            }
            order("occurred_on", Order.DESCENDING)
            order("created_at", Order.DESCENDING)
        }.decodeList<TransactionDto>().map { it.toDomain() }
    }

    override suspend fun createTransaction(command: NewTransaction): Transaction =
        client.from("transactions").insert(
            TransactionInsertDto(
                type = command.type.wire,
                amount = command.amount.toEuros(),
                fromWalletId = command.fromWalletId,
                toWalletId = command.toWalletId,
                categoryId = command.categoryId,
                occurredOn = command.occurredOn.toString(),
                note = command.note,
                createdBy = client.requireUserId(),
            ),
        ) { select() }.decodeSingle<TransactionDto>().toDomain()

    override suspend fun updateTransaction(command: UpdateTransaction) {
        // The `set` builder writes nulls explicitly so switching type clears the
        // wallet side the new type does not use — the schema's chk_wallets_by_type
        // rejects, say, an expense that still carries a to_wallet_id.
        client.from("transactions").update({
            set("type", command.type.wire)
            set("amount", command.amount.toEuros())
            set("from_wallet_id", command.fromWalletId)
            set("to_wallet_id", command.toWalletId)
            set("category_id", command.categoryId)
            set("occurred_on", command.occurredOn.toString())
            set("note", command.note)
        }) {
            filter { eq("id", command.id) }
        }
    }

    override suspend fun deleteTransaction(transactionId: String) {
        client.from("transactions").delete {
            filter { eq("id", transactionId) }
        }
    }

    private suspend fun walletIdsForSpace(spaceId: String): List<String> =
        client.from("wallets").select(Columns.list("id")) {
            filter { eq("space_id", spaceId) }
        }.decodeList<IdDto>().map { it.id }
}
