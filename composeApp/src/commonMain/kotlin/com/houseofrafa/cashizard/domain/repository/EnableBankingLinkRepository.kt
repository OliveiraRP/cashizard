package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.AspspOption

/**
 * Drives the Enable Banking account-linking flow through the `eb-aspsps` /
 * `eb-start-auth` Edge Functions — the app never holds the Enable Banking
 * private key, so it can't call Enable Banking's API directly.
 */
interface EnableBankingLinkRepository {
    suspend fun getAspsps(): List<AspspOption>

    /** Starts an authorization for [accountId] at the given bank; returns the URL to open. */
    suspend fun startAuth(accountId: String, aspspName: String, aspspCountry: String): String
}
