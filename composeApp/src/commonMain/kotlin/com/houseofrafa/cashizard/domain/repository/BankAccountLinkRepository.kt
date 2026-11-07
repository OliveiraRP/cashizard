package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.BankAccountLink
import com.houseofrafa.cashizard.domain.model.NewBankAccountLink

interface BankAccountLinkRepository {
    suspend fun getLinks(spaceId: String): List<BankAccountLink>

    suspend fun createLink(command: NewBankAccountLink): BankAccountLink

    suspend fun deleteLink(id: String)
}
