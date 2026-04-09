package com.houseofrafa.feature.wallet.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.houseofrafa.core.ui.theme.CashizardTheme
import com.houseofrafa.core.util.formatAsCurrency

private data class SummaryItem(val label: String, val amount: Double)

@Composable
fun WalletSummary(
    netWorth: Double,
    investmentsTotal: Double,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        SummaryItem(label = "Total Net Worth", amount = netWorth),
        SummaryItem(label = "Total Investments", amount = investmentsTotal),
    )
    val pagerState = rememberPagerState { items.size }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = modifier.fillMaxWidth(),
    ) {
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(vertical = CashizardTheme.spacing.sm),
            ) {
                Text(
                    text  = items[page].amount.formatAsCurrency(),
                    style = CashizardTheme.typography.displaySmall,
                    color = CashizardTheme.colors.textPrimary,
                )
                Text(
                    text     = items[page].label,
                    style    = CashizardTheme.typography.bodyMedium,
                    color    = CashizardTheme.colors.textSecondary,
                    modifier = Modifier.padding(top = CashizardTheme.spacing.xxs),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(CashizardTheme.spacing.xxs),
            modifier              = Modifier.padding(top = CashizardTheme.spacing.sm),
        ) {
            repeat(items.size) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color  = if (index == pagerState.currentPage) CashizardTheme.colors.brandPrimary
                                     else CashizardTheme.colors.textTertiary,
                            shape  = CircleShape,
                        )
                )
            }
        }
    }
}
