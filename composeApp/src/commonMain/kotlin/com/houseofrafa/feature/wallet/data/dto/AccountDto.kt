package com.houseofrafa.feature.wallet.data.dto

import com.houseofrafa.feature.wallet.domain.model.Account
import com.houseofrafa.feature.wallet.domain.model.Wallet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    @SerialName("id")       val id: String,
    @SerialName("user_id")  val userId: String,
    @SerialName("name")     val name: String,
    @SerialName("icon")     val icon: String,
    @SerialName("balance")  val balance: Double,
    @SerialName("archived") val archived: Boolean,
)

fun AccountDto.toDomain(wallets: List<Wallet> = emptyList()) = Account(
    id       = id,
    userId   = userId,
    name     = name,
    icon     = icon,
    balance  = balance,
    archived = archived,
    wallets  = wallets,
)
