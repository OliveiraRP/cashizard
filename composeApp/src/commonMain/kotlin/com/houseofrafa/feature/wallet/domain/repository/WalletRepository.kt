package com.houseofrafa.feature.wallet.domain.repository

import com.houseofrafa.feature.wallet.domain.model.Account

interface WalletRepository {
    suspend fun getAccounts(): Result<List<Account>>
}
