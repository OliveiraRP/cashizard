package com.houseofrafa.cashizard.domain.usecase

import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.SpaceTotals
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.repository.AccountRepository
import com.houseofrafa.cashizard.domain.repository.SpaceRepository
import com.houseofrafa.cashizard.domain.repository.WalletRepository

/** An account with the wallets that belong to it. */
data class AccountWithWallets(
    val account: Account,
    val wallets: List<Wallet>,
)

/** Everything the Wallets screen renders in one shot. */
data class WalletsOverview(
    val totals: SpaceTotals,
    val accounts: List<AccountWithWallets>,
    /** Wallets with no account (`wallets.account_id is null`). */
    val standaloneWallets: List<Wallet>,
)

/**
 * Assembles the wallet hierarchy for a space. Balances all come from the DB views —
 * this only groups and orders them.
 */
class GetWalletsOverview(
    private val spaceRepository: SpaceRepository,
    private val accountRepository: AccountRepository,
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(spaceId: String): WalletsOverview {
        val totals = spaceRepository.getSpaceTotals(spaceId)
        val accounts = accountRepository.getAccounts(spaceId)
        val wallets = walletRepository.getWallets(spaceId)

        val walletsByAccount = wallets.groupBy { it.accountId }

        return WalletsOverview(
            totals = totals,
            accounts = accounts.map { account ->
                AccountWithWallets(
                    account = account,
                    wallets = walletsByAccount[account.id].orEmpty(),
                )
            },
            standaloneWallets = walletsByAccount[null].orEmpty(),
        )
    }
}
