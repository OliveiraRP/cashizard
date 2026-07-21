package com.houseofrafa.cashizard.di

import com.houseofrafa.cashizard.domain.usecase.CreateTransaction
import com.houseofrafa.cashizard.domain.usecase.GetSelectableCategories
import com.houseofrafa.cashizard.domain.usecase.GetSpendingBreakdown
import com.houseofrafa.cashizard.domain.usecase.GetTransactionFeed
import com.houseofrafa.cashizard.domain.session.SpaceSession
import com.houseofrafa.cashizard.domain.usecase.GetWalletsOverview
import org.koin.dsl.module

val domainModule = module {
    // App-scoped: the active space and its change signal are shared by every screen.
    single { SpaceSession(get()) }

    factory { GetWalletsOverview(get(), get(), get()) }
    factory { GetTransactionFeed(get(), get(), get()) }
    factory { GetSpendingBreakdown(get(), get()) }
    factory { GetSelectableCategories(get()) }
    factory { CreateTransaction(get()) }
}
