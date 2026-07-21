package com.houseofrafa.cashizard

import androidx.compose.runtime.Composable
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.root.RootComponent
import com.houseofrafa.cashizard.presentation.root.RootContent

/** App root. The component tree is created by the platform entry point and passed in. */
@Composable
fun App(rootComponent: RootComponent) {
    CashizardTheme {
        RootContent(rootComponent)
    }
}
