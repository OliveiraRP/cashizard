package com.houseofrafa.cashizard.di

import com.houseofrafa.cashizard.data.remote.createCashizardSupabaseClient
import com.houseofrafa.cashizard.data.repository.AccountRepositoryImpl
import com.houseofrafa.cashizard.data.repository.AuthRepositoryImpl
import com.houseofrafa.cashizard.data.repository.CategoryRepositoryImpl
import com.houseofrafa.cashizard.data.repository.RecurringRuleRepositoryImpl
import com.houseofrafa.cashizard.data.repository.SpaceRepositoryImpl
import com.houseofrafa.cashizard.data.repository.TransactionRepositoryImpl
import com.houseofrafa.cashizard.data.repository.WalletRepositoryImpl
import com.houseofrafa.cashizard.domain.repository.AccountRepository
import com.houseofrafa.cashizard.domain.repository.AuthRepository
import com.houseofrafa.cashizard.domain.repository.CategoryRepository
import com.houseofrafa.cashizard.domain.repository.RecurringRuleRepository
import com.houseofrafa.cashizard.domain.repository.SpaceRepository
import com.houseofrafa.cashizard.domain.repository.TransactionRepository
import com.houseofrafa.cashizard.domain.repository.WalletRepository
import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module

val dataModule = module {
    single<SupabaseClient> { createCashizardSupabaseClient() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<SpaceRepository> { SpaceRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
    single<WalletRepository> { WalletRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<RecurringRuleRepository> { RecurringRuleRepositoryImpl(get()) }
}
