package com.houseofrafa.feature.wallet.data.repository

import com.houseofrafa.core.data.remote.supabase
import com.houseofrafa.feature.wallet.data.dto.AccountDto
import com.houseofrafa.feature.wallet.data.dto.WalletDto
import com.houseofrafa.feature.wallet.data.dto.toDomain
import com.houseofrafa.feature.wallet.domain.model.Account
import com.houseofrafa.feature.wallet.domain.repository.WalletRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest

class WalletRepositoryImpl : WalletRepository {

    override suspend fun getAccounts(): Result<List<Account>> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("No authenticated user")

        val accounts = supabase.postgrest
            .from("accounts")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("archived", false)
                }
            }
            .decodeList<AccountDto>()

        val wallets = supabase.postgrest
            .from("wallets")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("archived", false)
                }
            }
            .decodeList<WalletDto>()

        val walletsByAccount = wallets.groupBy { it.accountId }

        accounts.map { accountDto ->
            accountDto.toDomain(
                wallets = (walletsByAccount[accountDto.id] ?: emptyList()).map { it.toDomain() },
            )
        }
    }.onFailure { e ->
        println("WALLET ERROR: ${e::class.simpleName}: ${e.message}")
    }
}
