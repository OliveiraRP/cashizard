package com.houseofrafa.cashizard.presentation.arch

import androidx.lifecycle.ViewModel
import com.arkivanov.decompose.ComponentContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.mp.KoinPlatformTools

/**
 * Resolves a ViewModel from Koin and retains it for the life of this component.
 *
 * This is the only place the container is touched: navigation components are the
 * composition root, so ViewModels themselves keep plain constructors and stay
 * constructible in tests without Koin.
 *
 * [parameters] carries values only known at runtime, such as the active space id.
 */
inline fun <reified VM : ViewModel> ComponentContext.koinViewModel(
    key: String = VM::class.toString(),
    noinline parameters: ParametersDefinition? = null,
): VM = retainedViewModel(VM::class, key) {
    KoinPlatformTools.defaultContext().get().get<VM>(parameters = parameters)
}
