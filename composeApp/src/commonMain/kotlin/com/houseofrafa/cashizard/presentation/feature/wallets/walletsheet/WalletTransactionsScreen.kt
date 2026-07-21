package com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.houseofrafa.cashizard.domain.usecase.TransactionDay
import com.houseofrafa.cashizard.presentation.common.daySectionLabel
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/** One wallet's full history, grouped by day. */
@Composable
fun WalletTransactionsScreen(
    days: List<TransactionDay>,
    modifier: Modifier = Modifier,
) {
    val dimens = CashizardTheme.dimens

    if (days.isEmpty()) {
        EmptyHint("No transactions in this wallet yet.", modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = dimens.space8, bottom = dimens.space32),
    ) {
        days.forEach { day ->
            SectionHeader(title = day.date.daySectionLabel())
            FormCard(
                modifier = Modifier.padding(horizontal = dimens.listPadding),
                cornerRadius = dimens.radiusControl,
                separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
                rows = day.transactions.map { details ->
                    { WalletTxnRow(details, subtitle = details.rowSubtitle()) }
                },
            )
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}
