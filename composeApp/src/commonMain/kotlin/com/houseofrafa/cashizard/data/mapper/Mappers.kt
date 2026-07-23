package com.houseofrafa.cashizard.data.mapper

import com.houseofrafa.cashizard.data.dto.AccountBalanceDto
import com.houseofrafa.cashizard.data.dto.CategoryDto
import com.houseofrafa.cashizard.data.dto.CategoryGroupDto
import com.houseofrafa.cashizard.data.dto.RecurringRuleDto
import com.houseofrafa.cashizard.data.dto.SpaceTotalsDto
import com.houseofrafa.cashizard.data.dto.TransactionDto
import com.houseofrafa.cashizard.data.dto.WalletBalanceDto
import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.domain.model.RecurringRule
import com.houseofrafa.cashizard.domain.model.SpaceTotals
import com.houseofrafa.cashizard.domain.model.Transaction
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.model.WalletType
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

private fun Double.toMoney(): Money = Money.ofEuros(this)
private fun Double?.toMoneyOrNull(): Money? = this?.let { Money.ofEuros(it) }

fun SpaceTotalsDto.toDomain(): SpaceTotals = SpaceTotals(
    spaceId = spaceId,
    totalBalance = totalBalance.toMoney(),
    savingsInvestmentsBalance = savingsInvestmentsBalance.toMoney(),
)

fun AccountBalanceDto.toDomain(): Account = Account(
    id = id,
    spaceId = spaceId,
    name = name,
    icon = icon,
    sortOrder = sortOrder,
    balance = balance.toMoney(),
)

fun WalletBalanceDto.toDomain(): Wallet = Wallet(
    id = id,
    spaceId = spaceId,
    accountId = accountId,
    name = name,
    icon = icon,
    type = WalletType.fromWire(type),
    goalAmount = goalAmount.toMoneyOrNull(),
    annualBudget = annualBudget.toMoneyOrNull(),
    sortOrder = sortOrder,
    archived = archived,
    balance = balance.toMoney(),
    spentThisYear = spentThisYear.toMoneyOrNull(),
    annualBudgetLeft = annualBudgetLeft.toMoneyOrNull(),
)

fun CategoryGroupDto.toDomain(): CategoryGroup = CategoryGroup(
    id = id,
    spaceId = spaceId,
    name = name,
    color = color,
    type = TransactionType.fromWire(type),
    sortOrder = sortOrder,
)

fun CategoryDto.toDomain(): Category = Category(
    id = id,
    groupId = groupId,
    name = name,
    icon = icon,
    sortOrder = sortOrder,
    archived = archived,
    excludeFromAnalytics = excludeFromAnalytics,
)

fun TransactionDto.toDomain(): Transaction = Transaction(
    id = id,
    type = TransactionType.fromWire(type),
    amount = amount.toMoney(),
    fromWalletId = fromWalletId,
    toWalletId = toWalletId,
    categoryId = categoryId,
    occurredOn = LocalDate.parse(occurredOn),
    note = note,
    recurringRuleId = recurringRuleId,
    createdBy = createdBy,
    createdAt = Instant.parse(createdAt),
)

fun RecurringRuleDto.toDomain(): RecurringRule = RecurringRule(
    id = id,
    type = TransactionType.fromWire(type),
    amount = amount.toMoney(),
    fromWalletId = fromWalletId,
    toWalletId = toWalletId,
    categoryId = categoryId,
    note = note,
    dayOfMonth = dayOfMonth,
    nextRun = LocalDate.parse(nextRun),
    active = active,
    createdBy = createdBy,
)

/** Money -> decimal euros for numeric(12,2) columns. */
fun Money.toEuros(): Double = cents / 100.0
