package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.usecase.TransactionDetails
import com.houseofrafa.cashizard.presentation.common.dayMonthLabel
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.MicroProgressBar
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.format.formatEur
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import com.houseofrafa.cashizard.presentation.feature.wallets.label

/** A wallet's summary: icon, balance, progress, key facts and recent activity. */
@Composable
fun WalletDetailScreen(
    wallet: Wallet,
    accountName: String?,
    recent: List<TransactionDetails>,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = dimens.space32),
    ) {
        // Header block: disc, name, balance, optional progress.
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimens.space8),
        ) {
            IconDisc(
                icon = iconFor(wallet.icon),
                style = IconDiscStyle.Neutral,
                size = 56.dp,
                iconSize = 26.dp,
            )
            Text(
                text = wallet.name,
                style = CashizardTheme.typography.title3,
                color = colors.textPrimary,
            )
            Text(
                text = wallet.balance.formatEur(),
                style = CashizardTheme.typography.amountTotal,
                color = colors.textPrimary,
            )
            wallet.progress()?.let { progress ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.space8),
                    modifier = Modifier.padding(top = 2.dp),
                ) {
                    MicroProgressBar(
                        progress = progress.fraction,
                        modifier = Modifier.width(120.dp),
                    )
                    Text(
                        text = progress.label,
                        style = CashizardTheme.typography.caption,
                        color = colors.textTertiary,
                    )
                }
            }
        }

        // Key facts.
        SectionHeaderSpacer()
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16,
            rows = buildList {
                add { DetailFactRow("Type", wallet.type.label) }
                add { DetailFactRow("Account", accountName ?: "None") }
                wallet.amountFact()?.let { (label, value) ->
                    add { DetailFactRow(label, value.formatEur()) }
                }
            },
        )

        SectionHeader(
            title = "Recent transactions",
            style = CashizardTheme.typography.sectionLabelSmall,
            actionLabel = if (recent.isNotEmpty()) "See all" else null,
            onActionClick = onSeeAll.takeIf { recent.isNotEmpty() },
        )
        if (recent.isEmpty()) {
            EmptyHint("No transactions in this wallet yet.")
        } else {
            FormCard(
                modifier = Modifier.padding(horizontal = dimens.listPadding),
                cornerRadius = dimens.radiusControl,
                separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
                rows = recent.map { details ->
                    {
                        WalletTxnRow(
                            details,
                            subtitle = details.transaction.occurredOn.dayMonthLabel(),
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun DetailFactRow(label: String, value: String) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimens.rowHeightMin)
            .padding(horizontal = dimens.space16, vertical = dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = CashizardTheme.typography.bodyLarge,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = CashizardTheme.typography.bodyLarge,
            color = colors.textPrimary,
        )
    }
}
