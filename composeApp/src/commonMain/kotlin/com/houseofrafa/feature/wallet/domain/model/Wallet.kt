package com.houseofrafa.feature.wallet.domain.model

data class Wallet(
    val id: String,
    val userId: String,
    val accountId: String,
    val name: String,
    val walletType: WalletType,
    val balance: Double,
    val color: String,
    val icon: String,
    val includeNetWorth: Boolean,
    val archived: Boolean,
    /** Only set when [walletType] is [WalletType.BUDGET]. */
    val annualBudget: Double?,
    /** Only set when [walletType] is [WalletType.GOAL]. */
    val goal: Double?,
)
