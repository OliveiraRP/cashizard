package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.NewWallet
import com.houseofrafa.cashizard.domain.model.UpdateWallet
import com.houseofrafa.cashizard.domain.model.Wallet

interface WalletRepository {
    /** Non-archived wallets of a space with computed balances (wallet_balances view). */
    suspend fun getWallets(spaceId: String, includeArchived: Boolean = false): List<Wallet>

    suspend fun createWallet(command: NewWallet): Wallet

    suspend fun updateWallet(command: UpdateWallet): Wallet

    suspend fun setArchived(walletId: String, archived: Boolean)
}
