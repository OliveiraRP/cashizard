package com.houseofrafa.feature.wallet.data.dto

import com.houseofrafa.feature.wallet.domain.model.Wallet
import com.houseofrafa.feature.wallet.domain.model.WalletType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletDto(
    @SerialName("id")                val id: String,
    @SerialName("user_id")           val userId: String,
    @SerialName("account_id")        val accountId: String,
    @SerialName("name")              val name: String,
    @SerialName("wallet_type")       val walletType: String,
    @SerialName("balance")           val balance: Double,
    @SerialName("color")             val color: String,
    @SerialName("icon")              val icon: String,
    @SerialName("include_net_worth") val includeNetWorth: Boolean,
    @SerialName("archived")          val archived: Boolean,
    @SerialName("annual_budget")     val annualBudget: Double?,
    @SerialName("goal")              val goal: Double?,
)

fun WalletDto.toDomain() = Wallet(
    id              = id,
    userId          = userId,
    accountId       = accountId,
    name            = name,
    walletType      = WalletType.fromString(walletType),
    balance         = balance,
    color           = color,
    icon            = icon,
    includeNetWorth = includeNetWorth,
    archived        = archived,
    annualBudget    = annualBudget,
    goal            = goal,
)
