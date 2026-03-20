package com.houseofrafa.core.data.di

import com.houseofrafa.core.data.local.UserPreferences
import org.koin.dsl.module

val coreModule = module {
    single { UserPreferences() }
}