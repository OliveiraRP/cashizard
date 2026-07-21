package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.NewAccount
import com.houseofrafa.cashizard.domain.model.UpdateAccount

interface AccountRepository {
    /** Accounts of a space with reconciliation balances (account_balances view). */
    suspend fun getAccounts(spaceId: String): List<Account>

    suspend fun createAccount(command: NewAccount): Account

    suspend fun updateAccount(command: UpdateAccount): Account

    /**
     * Deletes an account. The `wallets.account_id` FK is `on delete set null`, so
     * its wallets are automatically left standalone rather than removed.
     */
    suspend fun deleteAccount(accountId: String)
}
