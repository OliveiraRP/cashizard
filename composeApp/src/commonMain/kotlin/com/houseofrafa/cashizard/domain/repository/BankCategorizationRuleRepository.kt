package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.BankCategorizationRule
import com.houseofrafa.cashizard.domain.model.NewBankCategorizationRule

interface BankCategorizationRuleRepository {
    suspend fun getRules(spaceId: String): List<BankCategorizationRule>

    /** Insert, or overwrite the existing rule for the same (space, matchText). */
    suspend fun upsertRule(command: NewBankCategorizationRule): BankCategorizationRule

    suspend fun deleteRule(id: String)
}
