package com.houseofrafa

import com.houseofrafa.core.data.di.coreModule
import com.houseofrafa.feature.auth.di.authModule
import com.houseofrafa.feature.wallet.di.walletModule
import org.koin.core.context.startKoin

fun initKoin() = startKoin {
    modules(coreModule, authModule, walletModule)
}