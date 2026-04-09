package com.houseofrafa.feature.wallet.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.houseofrafa.core.ui.compose.components.MainHeader
import com.houseofrafa.core.ui.compose.templates.MainScreenTemplate
import com.houseofrafa.core.ui.theme.CashizardTheme
import com.houseofrafa.feature.wallet.domain.model.Account
import com.houseofrafa.feature.wallet.presentation.components.AccountSection
import com.houseofrafa.feature.wallet.presentation.components.WalletSummary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WalletsScreen(
    viewModel: WalletsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    MainScreenTemplate {
        when {
            uiState.isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(color = CashizardTheme.colors.brandPrimary)
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text  = uiState.errorMessage!!,
                        style = CashizardTheme.typography.bodyMedium,
                        color = CashizardTheme.colors.feedbackError,
                    )
                }
            }

            else -> {
                MainHeader(
                    title    = "Wallets",
                    modifier = Modifier.fillMaxSize(),
                ) {
                    WalletsContent(
                        accounts         = uiState.accounts,
                        netWorth         = uiState.netWorth,
                        investmentsTotal = uiState.investmentsTotal,
                        modifier         = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletsContent(
    accounts: List<Account>,
    netWorth: Double,
    investmentsTotal: Double,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier       = modifier,
        contentPadding = PaddingValues(bottom = CashizardTheme.spacing.screenBottom),
    ) {
        item(key = "summary_cards") {
            WalletSummary(
                netWorth = netWorth,
                investmentsTotal = investmentsTotal,
                modifier = Modifier.padding(bottom = CashizardTheme.spacing.xl),
            )
        }
        accounts.forEach { account ->
            item(key = "account_${account.id}") {
                AccountSection(account = account)
            }
        }
    }
}
