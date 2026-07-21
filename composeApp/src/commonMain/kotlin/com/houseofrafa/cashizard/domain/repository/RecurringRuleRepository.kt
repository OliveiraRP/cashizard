package com.houseofrafa.cashizard.domain.repository

import com.houseofrafa.cashizard.domain.model.NewRecurringRule
import com.houseofrafa.cashizard.domain.model.RecurringRule

interface RecurringRuleRepository {
    suspend fun getRules(spaceId: String): List<RecurringRule>

    suspend fun createRule(command: NewRecurringRule): RecurringRule

    suspend fun setActive(ruleId: String, active: Boolean)

    suspend fun deleteRule(ruleId: String)
}
