package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.usecase.TransactionDay
import com.houseofrafa.cashizard.domain.usecase.TransactionDetails
import com.houseofrafa.cashizard.domain.usecase.WalletsOverview

data class WalletSheetUiState(
    val overview: WalletsOverview? = null,
    /** The whole space's feed, resolved once and filtered per wallet in the UI. */
    val feed: List<TransactionDay> = emptyList(),
    val editForm: EditWalletUiState? = null,
    val accountForm: EditAccountUiState? = null,
    val loading: Boolean = true,
    val errorMessage: String? = null,
    /** The sheet has asked to close; the host plays the dismiss animation. */
    val closeRequested: Boolean = false,
) {
    val wallets: List<Wallet>
        get() = overview?.let { it.accounts.flatMap { a -> a.wallets } + it.standaloneWallets }
            .orEmpty()

    val accounts: List<Account> get() = overview?.accounts?.map { it.account }.orEmpty()

    fun wallet(id: String): Wallet? = wallets.firstOrNull { it.id == id }

    fun account(id: String?): Account? = accounts.firstOrNull { it.id == id }

    /** This wallet's transactions, grouped by day, newest first. */
    fun walletFeed(walletId: String): List<TransactionDay> =
        feed.mapNotNull { day ->
            val rows = day.transactions.filter { it.touches(walletId) }
            if (rows.isEmpty()) null else TransactionDay(day.date, rows)
        }

    fun recentTransactions(walletId: String, limit: Int): List<TransactionDetails> =
        walletFeed(walletId).flatMap { it.transactions }.take(limit)
}

private fun TransactionDetails.touches(walletId: String): Boolean =
    transaction.fromWalletId == walletId || transaction.toWalletId == walletId
