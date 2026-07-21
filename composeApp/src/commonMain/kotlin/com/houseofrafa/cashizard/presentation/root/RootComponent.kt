package com.houseofrafa.cashizard.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.houseofrafa.cashizard.domain.model.AuthState
import com.houseofrafa.cashizard.presentation.arch.koinViewModel
import com.houseofrafa.cashizard.presentation.feature.auth.AuthComponent
import com.houseofrafa.cashizard.presentation.feature.main.MainComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable

/**
 * Top-level navigation. The stack is driven by the session rather than by
 * callbacks from the auth screen, so a restored session, a fresh login, a
 * sign-out, and an expired refresh token all land in the same place.
 */
class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val viewModel: RootViewModel = koinViewModel()

    val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Splash,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    init {
        viewModel.authState
            .onEach { authState ->
                val target = when (authState) {
                    AuthState.Unknown -> Config.Splash
                    AuthState.SignedOut -> Config.Auth
                    is AuthState.SignedIn -> Config.Main(authState.userId)
                }
                // replaceAll keeps the back stack from accumulating auth screens.
                navigation.replaceAll(target)
            }
            .launchIn(coroutineScope(Dispatchers.Main.immediate))
    }

    private fun createChild(config: Config, context: ComponentContext): Child = when (config) {
        Config.Splash -> Child.Splash
        Config.Auth -> Child.Auth(AuthComponent(context))
        is Config.Main -> Child.Main(MainComponent(context))
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Splash : Config

        @Serializable
        data object Auth : Config

        @Serializable
        data class Main(val userId: String) : Config
    }

    sealed interface Child {
        /** Shown while the persisted session is being restored. */
        data object Splash : Child

        data class Auth(val component: AuthComponent) : Child

        data class Main(val component: MainComponent) : Child
    }

    companion object {
        /**
         * Platform entry points call this so DI stays an implementation detail of
         * composeApp — the app modules never touch the container directly.
         */
        fun create(componentContext: ComponentContext): RootComponent =
            RootComponent(componentContext = componentContext)
    }
}
