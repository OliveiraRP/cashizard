package com.houseofrafa.feature.wallet.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.houseofrafa.core.ui.compose.components.SectionHeader
import com.houseofrafa.core.ui.icon.AppIcons
import com.houseofrafa.core.ui.theme.CashizardTheme
import com.houseofrafa.core.util.formatAsCurrency
import com.houseofrafa.feature.wallet.domain.model.Account

@Composable
fun AccountSection(
    account: Account,
    modifier: Modifier = Modifier,
) {
    SectionHeader(
        title        = account.name,
        icon         = AppIcons.fromName(account.icon),
        trailingText = account.balance.formatAsCurrency(),
        modifier     = modifier,
    ) {
        account.wallets.forEach { wallet ->
            WalletCard(
                wallet   = wallet,
                modifier = Modifier.padding(vertical = CashizardTheme.spacing.xxs),
            )
        }
        Spacer(Modifier.height(CashizardTheme.spacing.xl))
    }
}