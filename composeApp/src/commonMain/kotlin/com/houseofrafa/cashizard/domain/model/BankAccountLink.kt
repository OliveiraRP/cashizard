package com.houseofrafa.cashizard.domain.model

import kotlin.time.Instant

/**
 * A manual match between a real-world account and an Enable Banking account id.
 * The actual fetch/import job is not built yet — this just records which account
 * an external bank account feeds, ready for when it is.
 */
data class BankAccountLink(
    val id: String,
    val spaceId: String,
    val accountId: String,
    val enableBankingAccountId: String,
    val createdAt: Instant,
)

data class NewBankAccountLink(
    val spaceId: String,
    val accountId: String,
    val enableBankingAccountId: String,
)
