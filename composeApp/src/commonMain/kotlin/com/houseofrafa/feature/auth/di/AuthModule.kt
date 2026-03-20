package com.houseofrafa.feature.auth.di

import com.houseofrafa.feature.auth.data.repository.AuthRepositoryImpl
import com.houseofrafa.feature.auth.domain.repository.AuthRepository
import com.houseofrafa.feature.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { AuthViewModel(get()) }
}
