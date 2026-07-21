package com.houseofrafa.cashizard.presentation.feature.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.houseofrafa.cashizard.presentation.arch.koinViewModel
import kotlinx.serialization.Serializable

/**
 * The auth graph. Log in and sign up are separate screens with an iOS push
 * transition — the design gives sign up its own back chevron and headline
 * rather than toggling one form in place.
 *
 * Navigation only: each screen's state and logic live in its ViewModel.
 */
class AuthComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Login,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(config: Config, context: ComponentContext): Child = when (config) {
        Config.Login -> Child.Login(
            viewModel = context.koinViewModel(),
            // pushNew ignores a duplicate config, so a double tap cannot stack
            // two sign-up screens.
            onSignUpClick = { navigation.pushNew(Config.SignUp) },
        )

        Config.SignUp -> Child.SignUp(
            viewModel = context.koinViewModel(),
            onBack = navigation::pop,
        )
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Login : Config

        @Serializable
        data object SignUp : Config
    }

    sealed interface Child {
        data class Login(
            val viewModel: LoginViewModel,
            val onSignUpClick: () -> Unit,
        ) : Child

        data class SignUp(
            val viewModel: SignUpViewModel,
            val onBack: () -> Unit,
        ) : Child
    }
}
