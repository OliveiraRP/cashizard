package com.houseofrafa.cashizard.presentation.feature.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.houseofrafa.cashizard.domain.model.Money
import com.houseofrafa.cashizard.domain.model.Transaction
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.presentation.arch.koinViewModel
import com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction.AddTransactionSheetComponent
import com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction.EditingTransaction
import com.houseofrafa.cashizard.presentation.feature.analytics.AnalyticsViewModel
import com.houseofrafa.cashizard.presentation.feature.transactions.TransactionsViewModel
import com.houseofrafa.cashizard.presentation.feature.wallets.addaccount.AddAccountSheetComponent
import com.houseofrafa.cashizard.presentation.feature.wallets.addwallet.AddWalletSheetComponent
import com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet.WalletSheetComponent
import com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet.WalletSheetConfig
import com.houseofrafa.cashizard.presentation.feature.wallets.WalletsViewModel
import kotlinx.datetime.LocalDate

/**
 * The signed-in shell: a tab stack plus a slot for the modal page sheet.
 *
 * Navigation only. The shell's own state lives in [MainViewModel], and each tab
 * is a plain ViewModel because tabs have no navigation of their own.
 *
 * Tabs use `bringToFront` so each tab keeps its own component instance and
 * ViewModel when you switch away and back.
 */
class MainComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<MainConfig>()
    private val sheetNavigation = SlotNavigation<SheetConfig>()

    val viewModel: MainViewModel = koinViewModel()

    val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = MainConfig.serializer(),
        initialConfiguration = MainConfig.Transactions,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    val sheet: Value<ChildSlot<*, SheetChild>> = childSlot(
        source = sheetNavigation,
        serializer = SheetConfig.serializer(),
        key = "sheet",
        handleBackButton = false, // the sheet component owns its own back logic
        childFactory = ::createSheetChild,
    )

    fun onTabSelect(tab: MainTab) {
        navigation.bringToFront(
            when (tab) {
                MainTab.Transactions -> MainConfig.Transactions
                MainTab.Wallets -> MainConfig.Wallets
                MainTab.Analytics -> MainConfig.Analytics
            },
        )
    }

    fun onAddClick() = openSheet(SheetConfig.AddTransaction)

    /** Tapping a transaction opens the same sheet, seeded from that transaction. */
    fun onTransactionClick(transaction: Transaction) = openSheet(
        SheetConfig.EditTransaction(
            transactionId = transaction.id,
            type = transaction.type.wire,
            amountCents = transaction.amount.cents,
            fromWalletId = transaction.fromWalletId,
            toWalletId = transaction.toWalletId,
            categoryId = transaction.categoryId,
            occurredOn = transaction.occurredOn.toString(),
            note = transaction.note.orEmpty(),
        ),
    )

    fun onAddWalletClick() = openSheet(SheetConfig.AddWallet)

    fun onAddAccountClick() = openSheet(SheetConfig.AddAccount)

    fun onEditWalletsClick() = openSheet(SheetConfig.EditWallets)

    /** Tapping a wallet on the Wallets tab opens its detail sheet. */
    fun onWalletClick(walletId: String) = openSheet(SheetConfig.WalletDetails(walletId))

    /** No space means nothing to attach to, so the sheet stays closed. */
    private fun openSheet(config: SheetConfig) {
        if (viewModel.hasActiveSpace()) sheetNavigation.activate(config)
    }

    private fun createChild(config: MainConfig, context: ComponentContext): Child = when (config) {
        MainConfig.Transactions -> Child.Transactions(context.koinViewModel<TransactionsViewModel>())
        MainConfig.Wallets -> Child.Wallets(context.koinViewModel<WalletsViewModel>())
        MainConfig.Analytics -> Child.Analytics(context.koinViewModel<AnalyticsViewModel>())
    }

    private fun createSheetChild(
        config: SheetConfig,
        context: ComponentContext,
    ): SheetChild {
        val dismiss = { sheetNavigation.dismiss() }

        return when (config) {
            SheetConfig.AddTransaction -> SheetChild.AddTransaction(
                AddTransactionSheetComponent(context, onDismiss = dismiss),
            )

            is SheetConfig.EditTransaction -> SheetChild.AddTransaction(
                AddTransactionSheetComponent(
                    componentContext = context,
                    onDismiss = dismiss,
                    editing = EditingTransaction(
                        id = config.transactionId,
                        type = TransactionType.fromWire(config.type),
                        amount = Money(config.amountCents),
                        fromWalletId = config.fromWalletId,
                        toWalletId = config.toWalletId,
                        categoryId = config.categoryId,
                        occurredOn = LocalDate.parse(config.occurredOn),
                        note = config.note,
                    ),
                ),
            )

            SheetConfig.AddWallet -> SheetChild.AddWallet(
                AddWalletSheetComponent(context, dismiss),
            )

            SheetConfig.AddAccount -> SheetChild.AddAccount(
                AddAccountSheetComponent(context, dismiss),
            )

            SheetConfig.EditWallets -> SheetChild.WalletSheet(
                WalletSheetComponent(
                    componentContext = context,
                    initialConfig = WalletSheetConfig.EditWalletsPicker,
                    onDismiss = dismiss,
                ),
            )

            is SheetConfig.WalletDetails -> SheetChild.WalletSheet(
                WalletSheetComponent(
                    componentContext = context,
                    initialConfig = WalletSheetConfig.WalletDetails(config.walletId),
                    onDismiss = dismiss,
                ),
            )
        }
    }

    /** A tab currently on the stack, holding the ViewModel that drives it. */
    sealed interface Child {
        val tab: MainTab

        data class Transactions(val viewModel: TransactionsViewModel) : Child {
            override val tab: MainTab get() = MainTab.Transactions
        }

        data class Wallets(val viewModel: WalletsViewModel) : Child {
            override val tab: MainTab get() = MainTab.Wallets
        }

        data class Analytics(val viewModel: AnalyticsViewModel) : Child {
            override val tab: MainTab get() = MainTab.Analytics
        }
    }

    /** Sheets keep their own components: each hosts an internal push stack. */
    sealed interface SheetChild {
        data class AddTransaction(val component: AddTransactionSheetComponent) : SheetChild
        data class AddWallet(val component: AddWalletSheetComponent) : SheetChild
        data class AddAccount(val component: AddAccountSheetComponent) : SheetChild
        data class WalletSheet(val component: WalletSheetComponent) : SheetChild
    }
}
