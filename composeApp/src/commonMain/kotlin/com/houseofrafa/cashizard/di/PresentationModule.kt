package com.houseofrafa.cashizard.di

import com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction.AddTransactionViewModel
import com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction.EditingTransaction
import com.houseofrafa.cashizard.presentation.feature.analytics.AnalyticsViewModel
import com.houseofrafa.cashizard.presentation.feature.analytics.filtercategories.FilterCategoriesViewModel
import com.houseofrafa.cashizard.presentation.feature.auth.LoginViewModel
import com.houseofrafa.cashizard.presentation.feature.auth.SignUpViewModel
import com.houseofrafa.cashizard.presentation.feature.main.MainViewModel
import com.houseofrafa.cashizard.presentation.feature.transactions.TransactionsViewModel
import com.houseofrafa.cashizard.presentation.feature.wallets.addaccount.AddAccountViewModel
import com.houseofrafa.cashizard.presentation.feature.wallets.addwallet.AddWalletViewModel
import com.houseofrafa.cashizard.presentation.feature.wallets.walletsheet.WalletSheetViewModel
import com.houseofrafa.cashizard.presentation.feature.wallets.WalletsViewModel
import com.houseofrafa.cashizard.presentation.root.RootViewModel
import org.koin.dsl.module

/**
 * ViewModel wiring. Each ViewModel declares only the dependencies it actually
 * uses, so `get()` here is the one place that knows the whole graph.
 *
 * All ViewModels are `factory`: retention is the navigation component's job (see
 * `presentation/arch/RetainedViewModel.kt`), not the container's.
 */
val presentationModule = module {
    factory { RootViewModel(get()) }

    factory { LoginViewModel(get()) }
    factory { SignUpViewModel(get()) }

    factory { MainViewModel(get()) }
    factory { TransactionsViewModel(get(), get()) }
    factory { WalletsViewModel(get(), get()) }
    factory { AnalyticsViewModel(get(), get()) }
    factory { FilterCategoriesViewModel(get(), get()) }

    factory { AddAccountViewModel(get(), get()) }
    factory { AddWalletViewModel(get(), get(), get()) }
    factory { WalletSheetViewModel(get(), get(), get(), get(), get()) }

    // The transaction being edited is only known at navigation time; a new
    // transaction passes null and the sheet starts blank.
    factory { params ->
        AddTransactionViewModel(
            createTransaction = get(),
            transactionRepository = get(),
            walletRepository = get(),
            categoryRepository = get(),
            recurringRuleRepository = get(),
            spaceSession = get(),
            editing = params.getOrNull<EditingTransaction>(),
        )
    }
}
