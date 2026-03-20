package com.houseofrafa

import android.app.Application
import com.houseofrafa.initKoin

class CashizardApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}