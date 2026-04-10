package com.houseofrafa.feature.wallet.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.houseofrafa.core.ui.compose.components.HeaderButton
import com.houseofrafa.core.ui.compose.components.MainHeader
import com.houseofrafa.core.ui.compose.templates.MainScreenTemplate
import com.houseofrafa.core.ui.icon.AppIcons
import com.houseofrafa.core.ui.theme.CashizardTheme
import com.houseofrafa.feature.wallet.domain.model.Account
import com.houseofrafa.feature.wallet.presentation.preview.MockAccounts
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
                Column(modifier = Modifier.fillMaxSize()) {
                    MainHeader(
                        title       = "Wallets",
                        rightButton = {
                            HeaderButton(
                                icon    = AppIcons.Actions.Add,
                                onClick = {},
                            )
                        },
                    )
                    WalletsContent(
                        accounts         = uiState.accounts,
                        netWorth         = uiState.netWorth,
                        investmentsTotal = uiState.investmentsTotal,
                    )
                }
            }
        }
    }
}

@Composable
internal fun WalletsContent(
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
                netWorth         = netWorth,
                investmentsTotal = investmentsTotal,
            )
            Spacer(Modifier.height(CashizardTheme.spacing.xl))
        }
        accounts.forEach { account ->
            item(key = "account_${account.id}") {
                AccountSection(account = account)
            }
        }
    }
}

@Preview
@Composable
internal fun WalletsScreenPreview() {
    CashizardTheme(darkTheme = true) {
        MainScreenTemplate {
            Column(modifier = Modifier.fillMaxSize()) {
                MainHeader(
                    title       = "Wallets",
                    rightButton = { HeaderButton(icon = AppIcons.Actions.Add, onClick = {}) },
                )
                WalletsContent(
                    accounts         = MockAccounts.accounts,
                    netWorth         = MockAccounts.netWorth,
                    investmentsTotal = MockAccounts.investmentsTotal,
                )
            }
        }
    }
}
