package com.houseofrafa.cashizard.presentation.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.composables.icons.lucide.ChartPie
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ReceiptText
import com.composables.icons.lucide.Wallet
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.components.Fab
import com.houseofrafa.cashizard.presentation.designsystem.components.PageSheet
import com.houseofrafa.cashizard.presentation.designsystem.components.TabBar
import com.houseofrafa.cashizard.presentation.designsystem.components.TabItem
import com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction.AddTransactionSheetContent
import com.houseofrafa.cashizard.presentation.feature.analytics.AnalyticsScreen
import com.houseofrafa.cashizard.presentation.feature.transactions.TransactionsScreen
import com.houseofrafa.cashizard.presentation.feature.wallets.addaccount.AddAccountSheetContent
import com.houseofrafa.cashizard.presentation.feature.wallets.addwallet.AddWalletSheetContent
import com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet.WalletSheetContent
import com.houseofrafa.cashizard.presentation.feature.wallets.WalletsScreen

private val tabItems = listOf(
    TabItem("Transactions", Lucide.ReceiptText),
    TabItem("Wallets", Lucide.Wallet),
    TabItem("Analytics", Lucide.ChartPie),
)

/**
 * The signed-in shell: tab content, the tab bar, the floating add button, and
 * the page sheet layered above everything.
 */
@Composable
fun MainContent(component: MainComponent, modifier: Modifier = Modifier) {
    val colors = CashizardTheme.colors
    val stack by component.stack.subscribeAsState()
    val sheet by component.sheet.subscribeAsState()
    val activeSpace by component.viewModel.activeSpace.collectAsState()
    val activeTab = stack.active.instance.tab

    Box(modifier = modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.fillMaxSize()) {
            Children(
                stack = component.stack,
                modifier = Modifier.fillMaxSize().weight(1f),
                animation = stackAnimation(fade()),
            ) { child ->
                when (val instance = child.instance) {
                    is MainComponent.Child.Transactions ->
                        TransactionsScreen(
                            viewModel = instance.viewModel,
                            spaceName = activeSpace?.name,
                            onAddTransaction = component::onAddClick,
                            onTransactionClick = component::onTransactionClick,
                        )

                    is MainComponent.Child.Wallets ->
                        WalletsScreen(
                            viewModel = instance.viewModel,
                            spaceName = activeSpace?.name,
                            onAddWallet = component::onAddWalletClick,
                            onAddAccount = component::onAddAccountClick,
                            onEditWallets = component::onEditWalletsClick,
                            onWalletClick = component::onWalletClick,
                        )

                    is MainComponent.Child.Analytics ->
                        AnalyticsScreen(
                            viewModel = instance.viewModel,
                            spaceName = activeSpace?.name,
                        )
                }
            }

            TabBar(
                items = tabItems,
                selectedIndex = activeTab.ordinal,
                onSelect = { component.onTabSelect(MainTab.entries[it]) },
            )
        }

        // Sits above the tab bar, clear of the last row of content.
        Fab(
            onClick = component::onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = CashizardTheme.dimens.screenPadding, bottom = 106.dp),
        )

        // Each sheet reports "I want to close" through its own state, so the
        // dismiss animation is driven the same way regardless of which it is.
        when (val instance = sheet.child?.instance) {
            is MainComponent.SheetChild.AddTransaction -> {
                val state by instance.component.viewModel.state.collectAsState()
                PageSheet(
                    onDismissRequest = instance.component::onDismissed,
                    closeRequested = state.closeRequested,
                ) {
                    AddTransactionSheetContent(instance.component)
                }
            }

            is MainComponent.SheetChild.AddWallet -> {
                val state by instance.component.viewModel.state.collectAsState()
                PageSheet(
                    onDismissRequest = instance.component::onDismissed,
                    closeRequested = state.closeRequested,
                ) {
                    AddWalletSheetContent(instance.component)
                }
            }

            is MainComponent.SheetChild.AddAccount -> {
                val state by instance.component.viewModel.state.collectAsState()
                PageSheet(
                    onDismissRequest = instance.component::onDismissed,
                    closeRequested = state.closeRequested,
                ) {
                    AddAccountSheetContent(instance.component.viewModel)
                }
            }

            is MainComponent.SheetChild.WalletSheet -> {
                val state by instance.component.viewModel.state.collectAsState()
                PageSheet(
                    onDismissRequest = instance.component::onDismissed,
                    closeRequested = state.closeRequested,
                ) {
                    WalletSheetContent(instance.component)
                }
            }

            null -> Unit
        }
    }
}

