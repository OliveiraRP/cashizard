package com.houseofrafa.feature.wallet.presentation.preview

import com.houseofrafa.feature.wallet.domain.model.Account
import com.houseofrafa.feature.wallet.domain.model.Wallet
import com.houseofrafa.feature.wallet.domain.model.WalletType

object MockAccounts {

    val accounts = listOf(
        Account(
            id = "1",
            userId = "u1",
            name = "Personal",
            icon = "wallet",
            balance = 4_250.00,
            archived = false,
            wallets = listOf(
                Wallet(
                    id = "w1",
                    userId = "u1",
                    accountId = "1",
                    name = "Checking",
                    walletType = WalletType.EXPENSE,
                    balance = 2_100.00,
                    color = "#BF5AF2",
                    icon = "creditcard",
                    includeNetWorth = true,
                    archived = false,
                    annualBudget = null,
                    goal = null,
                ),
                Wallet(
                    id = "w2",
                    userId = "u1",
                    accountId = "1",
                    name = "Savings",
                    walletType = WalletType.SAVINGS,
                    balance = 2_150.00,
                    color = "#30D158",
                    icon = "savings",
                    includeNetWorth = true,
                    archived = false,
                    annualBudget = null,
                    goal = null,
                ),
            ),
        ),
        Account(
            id = "2",
            userId = "u1",
            name = "Investments",
            icon = "briefcase",
            balance = 12_800.00,
            archived = false,
            wallets = listOf(
                Wallet(
                    id = "w3",
                    userId = "u1",
                    accountId = "2",
                    name = "Stock Portfolio",
                    walletType = WalletType.INVESTMENTS,
                    balance = 12_800.00,
                    color = "#0A84FF",
                    icon = "star",
                    includeNetWorth = true,
                    archived = false,
                    annualBudget = null,
                    goal = null,
                ),
            ),
        ),
    )

    val netWorth         = 17_050.00
    val investmentsTotal = 12_800.00
}