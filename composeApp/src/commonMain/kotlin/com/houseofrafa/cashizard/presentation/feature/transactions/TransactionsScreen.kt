package com.houseofrafa.cashizard.presentation.feature.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ReceiptText
import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.domain.model.Transaction
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.usecase.TransactionDay
import com.houseofrafa.cashizard.domain.usecase.TransactionDetails
import com.houseofrafa.cashizard.presentation.common.ScreenHeader
import com.houseofrafa.cashizard.presentation.common.daySectionLabel
import com.houseofrafa.cashizard.presentation.common.monthYearLabel
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.format.formatEur
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    spaceName: String?,
    onAddTransaction: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
    ) {
        ScreenHeader(
            title = "Transactions",
            spaceName = spaceName,
            modifier = Modifier.padding(top = dimens.space16),
        )

        MonthSwitcher(
            label = state.month.monthYearLabel(),
            onPrevious = viewModel::onPreviousMonth,
            onNext = viewModel::onNextMonth,
        )

        when {
            state.loading && state.days.isEmpty() -> CenteredBox {
                CircularProgressIndicator(
                    color = colors.accent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(28.dp),
                )
            }

            state.errorMessage != null -> CenteredBox {
                Text(
                    text = state.errorMessage.orEmpty(),
                    style = CashizardTheme.typography.footnote,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                )
            }

            state.isEmpty -> EmptyMonth(onAddTransaction)

            else -> Feed(state, onTransactionClick)
        }
    }
}

@Composable
private fun MonthSwitcher(label: String, onPrevious: () -> Unit, onNext: () -> Unit) {
    val colors = CashizardTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MonthChevron(Lucide.ChevronLeft, "Previous month", onPrevious)
        Text(
            text = label,
            style = CashizardTheme.typography.headline,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.width(160.dp),
        )
        MonthChevron(Lucide.ChevronRight, "Next month", onNext)
    }
}

@Composable
private fun MonthChevron(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = CashizardTheme.colors.accent,
        modifier = Modifier
            .size(20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    )
}

@Composable
private fun Feed(state: TransactionsUiState, onTransactionClick: (Transaction) -> Unit) {
    val dimens = CashizardTheme.dimens

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        MonthSummary(state.totals)

        state.days.forEach { day ->
            SectionHeader(title = day.date.daySectionLabel())
            DayCard(day, onTransactionClick)
        }

        // Clears the floating button and the tab bar.
        Spacer(Modifier.height(120.dp))
    }
}

/** Income / expenses / net for the month, split by vertical hairlines. */
@Composable
private fun MonthSummary(totals: MonthTotals) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimens.listPadding, end = dimens.listPadding, top = 6.dp)
            .background(colors.surface, RoundedCornerShape(dimens.radiusCard))
            .padding(vertical = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SummaryCell("Income", totals.income.formatEur(withSign = true), colors.positive, Modifier.weight(1f))
        SummaryDivider()
        SummaryCell("Expenses", "−${totals.expenses.formatEur()}", colors.negative, Modifier.weight(1f))
        SummaryDivider()
        SummaryCell("Net", totals.net.formatEur(withSign = true), colors.textPrimary, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCell(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = CashizardTheme.typography.caption2.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            ),
            color = CashizardTheme.colors.textTertiary,
        )
        Text(
            text = value,
            style = CashizardTheme.typography.amountSmall,
            color = valueColor,
        )
    }
}

@Composable
private fun SummaryDivider() {
    Box(
        Modifier
            .width(0.5.dp)
            .height(34.dp)
            .background(CashizardTheme.colors.separator),
    )
}

@Composable
private fun DayCard(day: TransactionDay, onTransactionClick: (Transaction) -> Unit) {
    val dimens = CashizardTheme.dimens
    FormCard(
        modifier = Modifier.padding(horizontal = dimens.listPadding),
        cornerRadius = dimens.radiusCard,
        separatorInset = dimens.space16 + dimens.iconDisc + dimens.space12,
        rows = day.transactions.map { details ->
            { TransactionRow(details, onClick = { onTransactionClick(details.transaction) }) }
        },
    )
}

@Composable
private fun TransactionRow(details: TransactionDetails, onClick: () -> Unit) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val transaction = details.transaction
    val isTransfer = transaction.type == TransactionType.TRANSFER

    val amountColor = when (transaction.type) {
        TransactionType.EXPENSE -> colors.negative
        TransactionType.INCOME -> colors.positive
        TransactionType.TRANSFER -> colors.textSecondary
    }
    val amountText = when (transaction.type) {
        TransactionType.EXPENSE -> "−${transaction.amount.formatEur()}"
        TransactionType.INCOME -> transaction.amount.formatEur(withSign = true)
        TransactionType.TRANSFER -> transaction.amount.formatEur()
    }

    CashListRow(
        title = details.rowTitle(),
        subtitle = details.rowSubtitle(),
        minHeight = 0.dp,
        verticalPadding = 10.dp,
        onClick = onClick,
        leading = {
            if (isTransfer) {
                IconDisc(
                    icon = iconFor("arrow-right-left"),
                    color = colors.surfaceRaised,
                    style = IconDiscStyle.Solid,
                    size = dimens.iconDisc,
                    iconSize = 18.dp,
                )
            } else {
                IconDisc(
                    icon = iconFor(details.category?.icon),
                    color = CategoryColors.parse(details.group?.color),
                    style = IconDiscStyle.Solid,
                    size = dimens.iconDisc,
                    iconSize = 18.dp,
                )
            }
        },
        trailing = {
            Text(text = amountText, style = CashizardTheme.typography.amount, color = amountColor)
        },
    )
}

/** Transfers read as "source → destination"; everything else is its category. */
private fun TransactionDetails.rowTitle(): String =
    if (transaction.type == TransactionType.TRANSFER) {
        "${fromWallet?.name.orEmpty()} → ${toWallet?.name.orEmpty()}"
    } else {
        category?.name ?: "Uncategorised"
    }

/** The note, or nothing — the wallet is implied by the screen and left off the row. */
private fun TransactionDetails.rowSubtitle(): String? =
    transaction.note?.takeIf { it.isNotBlank() }

@Composable
private fun EmptyMonth(onAddTransaction: () -> Unit) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = dimens.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(dimens.iconDiscEmpty)
                .background(colors.surfaceChip, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Lucide.ReceiptText,
                contentDescription = null,
                tint = colors.textPlaceholder,
                modifier = Modifier.size(30.dp),
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "No transactions yet",
            style = CashizardTheme.typography.headline,
            color = colors.textPrimary,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Nothing recorded this month. Add your first expense or income.",
            style = CashizardTheme.typography.bodySmall,
            color = colors.textTertiary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = "Add transaction",
            style = CashizardTheme.typography.subhead,
            color = colors.accent,
            modifier = Modifier
                .background(colors.accentSubtle, RoundedCornerShape(dimens.radiusPill))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddTransaction,
                )
                .padding(horizontal = dimens.space20, vertical = 9.dp),
        )
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(CashizardTheme.dimens.space32),
        contentAlignment = Alignment.Center,
    ) { content() }
}
