package com.houseofrafa.cashizard

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.houseofrafa.cashizard.di.initKoin
import com.houseofrafa.cashizard.presentation.root.RootComponent
import platform.UIKit.UIViewController

private var koinStarted = false

/**
 * iOS entry point. DI starts here rather than in Swift so the Kotlin side owns
 * its own initialization; the lifecycle is owned here too because UIKit has no
 * equivalent of Android's retained component.
 */
fun MainViewController(): UIViewController {
    if (!koinStarted) {
        initKoin()
        koinStarted = true
    }

    val lifecycle = LifecycleRegistry()
    val root = RootComponent.create(DefaultComponentContext(lifecycle = lifecycle))

    return ComposeUIViewController { App(root) }
}
