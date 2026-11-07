package com.houseofrafa.cashizard.domain.model

/**
 * A rule the sync job checks every bank transaction's [description] against,
 * case-insensitive substring match. One rule per (space, matchText) — saving
 * again with the same text overwrites the category/wallet rather than adding
 * a duplicate.
 */
data class BankCategorizationRule(
    val id: String,
    val spaceId: String,
    val matchText: String,
    val categoryId: String,
    val walletId: String,
)

data class NewBankCategorizationRule(
    val spaceId: String,
    val matchText: String,
    val categoryId: String,
    val walletId: String,
)
