package com.houseofrafa.feature.wallet.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.houseofrafa.core.ui.toComposeColor
import com.houseofrafa.core.ui.compose.components.AppCard
import com.houseofrafa.core.ui.compose.components.IconBox
import com.houseofrafa.core.ui.icon.AppIcons
import com.houseofrafa.core.ui.theme.CashizardTheme
import com.houseofrafa.core.util.formatAsCurrency
import com.houseofrafa.feature.wallet.domain.model.Wallet

@Composable
fun WalletCard(
    wallet: Wallet,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconBox(
                icon            = AppIcons.fromName(wallet.icon),
                backgroundColor = wallet.color.toComposeColor(),
            )

            Spacer(Modifier.width(CashizardTheme.spacing.sm))

            Column(
                verticalArrangement = Arrangement.spacedBy(CashizardTheme.spacing.xxxs),
                modifier            = Modifier.weight(1f),
            ) {
                Text(
                    text  = wallet.name,
                    style = CashizardTheme.typography.bodyMedium,
                    color = CashizardTheme.colors.textPrimary,
                )
                Text(
                    text  = wallet.walletType.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = CashizardTheme.typography.bodySmall,
                    color = CashizardTheme.colors.textSecondary,
                )
            }

            Text(
                text  = wallet.balance.formatAsCurrency(),
                style = CashizardTheme.typography.titleMedium,
                color = CashizardTheme.colors.textPrimary,
            )
        }
    }
}
