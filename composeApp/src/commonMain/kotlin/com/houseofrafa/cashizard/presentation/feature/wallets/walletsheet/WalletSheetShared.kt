package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.domain.usecase.TransactionDetails
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.format.formatEur
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import kotlin.math.roundToInt

/** A transaction row sized for the wallet sheets (30dp disc), amount colored by type. */
@Composable
internal fun WalletTxnRow(details: TransactionDetails, subtitle: String?) {
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
        title = details.walletRowTitle(),
        titleStyle = CashizardTheme.typography.bodyLarge,
        subtitle = subtitle,
        minHeight = dimens.rowHeight,
        verticalPadding = 6.dp,
        leading = {
            IconDisc(
                icon = if (isTransfer) iconFor("arrow-right-left") else iconFor(details.category?.icon),
                color = if (isTransfer) colors.surfaceRaised else CategoryColors.parse(details.group?.color),
                style = IconDiscStyle.Solid,
                size = dimens.iconDiscWallet,
                iconSize = dimens.space16,
            )
        },
        trailing = {
            Text(text = amountText, style = CashizardTheme.typography.amount, color = amountColor)
        },
    )
}

/** Transfers read "source → destination"; everything else is its category. */
internal fun TransactionDetails.walletRowTitle(): String =
    if (transaction.type == TransactionType.TRANSFER) {
        "${fromWallet?.name.orEmpty()} → ${toWallet?.name.orEmpty()}"
    } else {
        category?.name ?: "Uncategorised"
    }

/** For the grouped all-transactions list, the day header already carries the date. */
internal fun TransactionDetails.rowSubtitle(): String? {
    if (transaction.type == TransactionType.TRANSFER) return "Transfer"
    return transaction.note?.takeIf { it.isNotBlank() }
}

@Composable
internal fun SectionHeaderSpacer() {
    Spacer(Modifier.height(CashizardTheme.dimens.space20))
}

@Composable
internal fun EmptyHint(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(CashizardTheme.dimens.space32),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = CashizardTheme.typography.footnote,
            color = CashizardTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
        )
    }
}

/** A full-width red text button on a card, used for the forms' archive actions. */
@Composable
internal fun DangerRowButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    Box(
        modifier = modifier
            .padding(horizontal = dimens.listPadding)
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimens.radiusControl))
            .background(colors.surface)
            .defaultMinSize(minHeight = dimens.rowHeightCompact)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = CashizardTheme.typography.bodyLarge,
            color = colors.negative,
        )
    }
}

internal data class WalletProgress(val fraction: Float, val label: String)

/** Goal and budget wallets get a progress bar; the rest none. */
internal fun Wallet.progress(): WalletProgress? = when (type) {
    WalletType.GOAL -> goalAmount?.let { goal ->
        WalletProgress(
            goalProgress,
            "${balance.formatEur(showCents = false)} of " +
                "${goal.formatEur(showCents = false)} · ${(goalProgress * 100).roundToInt()} %",
        )
    }

    WalletType.BUDGET -> annualBudget?.let { budget ->
        val spent = spentThisYear ?: Money.Zero
        val fraction = if (budget.cents <= 0) {
            0f
        } else {
            (spent.cents.toFloat() / budget.cents.toFloat()).coerceIn(0f, 1f)
        }
        WalletProgress(
            fraction,
            "${spent.formatEur(showCents = false)} of " +
                "${budget.formatEur(showCents = false)} · ${(fraction * 100).roundToInt()} %",
        )
    }

    else -> null
}

/** The amount fact shown in the detail card, if the type has one. */
internal fun Wallet.amountFact(): Pair<String, Money>? = when (type) {
    WalletType.GOAL -> goalAmount?.let { "Goal amount" to it }
    WalletType.BUDGET -> annualBudget?.let { "Annual budget" to it }
    else -> null
}
