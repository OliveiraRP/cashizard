package com.houseofrafa.cashizard.presentation.feature.wallets

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.domain.model.Wallet
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.domain.usecase.AccountWithWallets
import com.houseofrafa.cashizard.domain.usecase.WalletsOverview
import com.composables.icons.lucide.Ellipsis
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Wallet as WalletIcon
import com.houseofrafa.cashizard.presentation.common.ScreenHeader
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.PopoverMenu
import com.houseofrafa.cashizard.presentation.designsystem.components.PopoverMenuItem
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.MicroProgressBar
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.format.formatEur
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import kotlin.math.roundToInt

@Composable
fun WalletsScreen(
    viewModel: WalletsViewModel,
    spaceName: String?,
    onAddWallet: () -> Unit,
    onAddAccount: () -> Unit,
    onEditWallets: () -> Unit,
    onWalletClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    var menuOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            ScreenHeader(
                title = "Wallets",
                spaceName = spaceName,
                modifier = Modifier.padding(top = dimens.space16),
                trailing = {
                    CircleIconButton(
                        icon = Lucide.Ellipsis,
                        onClick = { menuOpen = true },
                        contentDescription = "Wallet options",
                        size = 32.dp,
                        iconSize = 18.dp,
                    )
                },
            )

            when {
                state.loading && state.overview == null -> LoadingState()
                state.errorMessage != null -> MessageState(state.errorMessage.orEmpty())
                state.overview != null -> WalletsContent(state.overview!!, onWalletClick)
            }
        }

        if (menuOpen) {
            // A full-screen catcher closes the menu on any outside tap, which is
            // how iOS context menus behave.
            Box(
                modifier = Modifier.fillMaxSize().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { menuOpen = false },
                ),
            )
            PopoverMenu(
                items = listOf(
                    PopoverMenuItem("Add wallet account", Lucide.Landmark) {
                        menuOpen = false
                        onAddAccount()
                    },
                    PopoverMenuItem("Add wallet", Lucide.WalletIcon) {
                        menuOpen = false
                        onAddWallet()
                    },
                    PopoverMenuItem("Edit wallets", Lucide.Pencil) {
                        menuOpen = false
                        onEditWallets()
                    },
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 68.dp, end = dimens.screenPadding),
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = CashizardTheme.colors.accent,
            strokeWidth = 2.dp,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun MessageState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(CashizardTheme.dimens.space32),
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

@Composable
private fun WalletsContent(overview: WalletsOverview, onWalletClick: (String) -> Unit) {
    val dimens = CashizardTheme.dimens

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        TotalBalance(
            total = overview.totals.totalBalance,
            savings = overview.totals.savingsInvestmentsBalance,
        )

        overview.accounts.forEach { account ->
            AccountCard(account, onWalletClick, modifier = Modifier.padding(top = 18.dp))
        }

        if (overview.standaloneWallets.isNotEmpty()) {
            SectionHeader(title = "Other wallets", modifier = Modifier.padding(top = 2.dp))
            WalletCard(overview.standaloneWallets, onWalletClick)
        }

        // Clears the floating button and the tab bar.
        Spacer(Modifier.height(120.dp))
    }
}

@Composable
private fun TotalBalance(total: Money, savings: Money) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = dimens.screenPadding, end = dimens.screenPadding, top = 14.dp),
    ) {
        Text(
            text = "Total balance",
            style = CashizardTheme.typography.footnote,
            color = colors.textTertiary,
        )
        Text(
            text = total.formatEur(),
            style = CashizardTheme.typography.amountTotal,
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 2.dp),
        )
        Row(modifier = Modifier.padding(top = 3.dp)) {
            Text(
                text = savings.formatEur(),
                style = CashizardTheme.typography.footnote.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                ),
                color = colors.textSecondary,
            )
            Text(
                text = " in savings & investments",
                style = CashizardTheme.typography.footnote,
                color = colors.textTertiary,
            )
        }
    }
}

/** An account and the wallets under it, with the account's rolled-up balance. */
@Composable
private fun AccountCard(
    account: AccountWithWallets,
    onWalletClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    FormCard(
        modifier = modifier.padding(horizontal = dimens.listPadding),
        cornerRadius = dimens.radiusCardLarge,
        separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
        rows = buildList {
            add {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.accountHeader)
                            .padding(horizontal = dimens.space16, vertical = dimens.space12),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = account.account.name.uppercase(),
                            style = CashizardTheme.typography.sectionLabel,
                            color = colors.textTertiary,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = account.account.balance.formatEur(),
                            style = CashizardTheme.typography.amountHeader,
                            color = colors.textPrimary,
                        )
                    }
                    // Full-width rule under the account header, unlike the inset
                    // hairlines between wallet rows.
                    Box(
                        Modifier.fillMaxWidth().height(0.5.dp).background(colors.separator),
                    )
                }
            }
            account.wallets.forEach { wallet -> add { WalletRow(wallet, onWalletClick) } }
        },
    )
}

@Composable
private fun WalletCard(
    wallets: List<Wallet>,
    onWalletClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = CashizardTheme.dimens
    FormCard(
        modifier = modifier.padding(horizontal = dimens.listPadding),
        cornerRadius = dimens.radiusCardLarge,
        separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
        rows = wallets.map { wallet -> { WalletRow(wallet, onWalletClick) } },
    )
}

/**
 * A wallet row. Budget wallets show what is left of the annual budget; goal
 * wallets show progress toward the goal; the rest show only their balance.
 */
@Composable
private fun WalletRow(wallet: Wallet, onWalletClick: (String) -> Unit) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    val isGoal = wallet.type == WalletType.GOAL && wallet.goalAmount != null

    CashListRow(
        title = wallet.name,
        titleStyle = CashizardTheme.typography.bodyLarge,
        subtitle = wallet.budgetLeftLabel(),
        subtitleStyle = CashizardTheme.typography.caption,
        subtitleSlot = if (isGoal) ({ GoalProgress(wallet) }) else null,
        minHeight = dimens.rowHeight,
        verticalPadding = 6.dp,
        onClick = { onWalletClick(wallet.id) },
        leading = {
            IconDisc(
                icon = iconFor(wallet.icon),
                style = IconDiscStyle.Neutral,
                size = dimens.iconDiscWallet,
                iconSize = dimens.space16,
            )
        },
        trailing = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = wallet.balance.formatEur(),
                    style = CashizardTheme.typography.amount,
                    color = colors.textPrimary,
                )
            }
        },
    )
}

/** The goal wallet's progress bar, sitting under its row's title. */
@Composable
private fun GoalProgress(wallet: Wallet) {
    val colors = CashizardTheme.colors

    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        MicroProgressBar(
            progress = wallet.goalProgress,
            modifier = Modifier.width(90.dp),
        )
        Text(
            text = "${(wallet.goalProgress * 100).roundToInt()} %",
            style = CashizardTheme.typography.caption2,
            color = colors.textTertiary,
        )
    }
}

/** "2.100 € left" for budget wallets; nothing for the other types. */
private fun Wallet.budgetLeftLabel(): String? {
    if (type != WalletType.BUDGET) return null
    val left = annualBudgetLeft ?: return null
    return "${left.formatEur(showCents = false)} left"
}
