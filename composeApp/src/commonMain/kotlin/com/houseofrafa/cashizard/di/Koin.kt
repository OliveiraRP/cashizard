package com.houseofrafa.cashizard.di

import org.koin.core.context.startKoin

/**
 * Single entry point for DI startup. Android calls this from Application.onCreate,
 * iOS from the app delegate before the Compose view controller is created.
 */
fun initKoin() {
    startKoin {
        modules(dataModule, domainModule, presentationModule)
    }
}
