package com.houseofrafa.feature.wallet.domain.model

data class Account(
    val id: String,
    val userId: String,
    val name: String,
    val icon: String,
    val balance: Double,
    val archived: Boolean,
    val wallets: List<Wallet> = emptyList(),
)
