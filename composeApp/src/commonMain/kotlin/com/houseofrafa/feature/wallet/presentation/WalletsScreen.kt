package com.houseofrafa.feature.wallet.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.houseofrafa.core.ui.compose.components.SectionHeader
import com.houseofrafa.core.ui.compose.templates.MainScreenTemplate
import com.houseofrafa.core.ui.icon.AppIcons
import com.houseofrafa.core.ui.theme.CashizardTheme
import com.houseofrafa.core.util.formatAsCurrency
import com.houseofrafa.feature.wallet.domain.model.Account
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
                        accounts = uiState.accounts,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletsContent(
    accounts: List<Account>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier       = modifier,
        contentPadding = PaddingValues(bottom = CashizardTheme.spacing.screenBottom),
    ) {
        accounts.forEach { account ->
            item(key = "account_${account.id}") {
                AccountSectionHeader(account = account) {
                    account.wallets.forEach { wallet ->
                        WalletCard(
                            wallet   = wallet,
                            modifier = Modifier.padding(vertical = CashizardTheme.spacing.xxs),
                        )
                    }
                    Spacer(Modifier.height(CashizardTheme.spacing.xl))
                }
            }
        }
    }
}

@Composable
private fun AccountSectionHeader(
    account: Account,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    SectionHeader(
        title        = account.name,
        icon         = AppIcons.fromName(account.icon),
        trailingText = account.balance.formatAsCurrency(),
        modifier     = modifier,
        content      = content,
    )
}
