package com.houseofrafa.cashizard.presentation.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.feature.auth.AuthContent
import com.houseofrafa.cashizard.presentation.feature.main.MainContent

/**
 * Renders the root stack. Auth <-> Main cross-fade rather than push, since
 * neither is "deeper" than the other.
 */
@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier.fillMaxSize().background(CashizardTheme.colors.background),
        animation = stackAnimation(fade()),
    ) { child ->
        when (val instance = child.instance) {
            RootComponent.Child.Splash -> SplashContent()
            is RootComponent.Child.Auth -> AuthContent(instance.component)
            is RootComponent.Child.Main -> MainContent(instance.component)
        }
    }
}

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier.fillMaxSize().background(CashizardTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = CashizardTheme.colors.accent,
            strokeWidth = 2.dp,
            modifier = Modifier.size(32.dp),
        )
    }
}

