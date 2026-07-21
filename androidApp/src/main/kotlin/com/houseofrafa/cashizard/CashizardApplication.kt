package com.houseofrafa.cashizard

import android.app.Application
import com.houseofrafa.cashizard.di.initKoin

class CashizardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
