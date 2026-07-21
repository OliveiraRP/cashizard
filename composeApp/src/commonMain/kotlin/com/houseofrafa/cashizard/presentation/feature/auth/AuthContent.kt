package com.houseofrafa.cashizard.presentation.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/** Renders the auth stack with an iOS-style horizontal push between screens. */
@Composable
fun AuthContent(component: AuthComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier.fillMaxSize().background(CashizardTheme.colors.background),
        animation = stackAnimation(slide()),
    ) { child ->
        when (val instance = child.instance) {
            is AuthComponent.Child.Login ->
                LoginScreen(instance.viewModel, instance.onSignUpClick)

            is AuthComponent.Child.SignUp ->
                SignUpScreen(instance.viewModel, instance.onBack)
        }
    }
}
