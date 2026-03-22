package com.houseofrafa.feature.wallet.di

import com.houseofrafa.feature.wallet.data.repository.WalletRepositoryImpl
import com.houseofrafa.feature.wallet.domain.repository.WalletRepository
import com.houseofrafa.feature.wallet.presentation.WalletsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val walletModule = module {
    singleOf(::WalletRepositoryImpl) bind WalletRepository::class
    viewModelOf(::WalletsViewModel)
}
