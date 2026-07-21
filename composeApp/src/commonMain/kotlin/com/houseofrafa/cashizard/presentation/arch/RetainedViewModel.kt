package com.houseofrafa.cashizard.presentation.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlin.reflect.KClass

/**
 * Bridges Decompose's retention to androidx ViewModels.
 *
 * A component's [InstanceKeeper] already survives configuration changes, so it
 * is the natural owner of a [ViewModelStore]. Clearing the store on destroy is
 * what invokes `onCleared` and cancels `viewModelScope`, which is why the store
 * is held here rather than the ViewModel itself — `onCleared` is protected and
 * cannot be called directly.
 */
private class RetainedViewModelStore : InstanceKeeper.Instance {
    val store = ViewModelStore()

    override fun onDestroy() {
        store.clear()
    }
}

/** Hands back one already-built ViewModel; the store owns it from then on. */
private class SingleViewModelFactory(
    private val build: () -> ViewModel,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
        build() as T
}

/**
 * Creates [build]'s ViewModel once and returns the same instance for the life of
 * this component, across configuration changes.
 *
 * [key] only needs setting when one component hosts two ViewModels of the same
 * type, since the store keys by type name otherwise.
 */
fun <VM : ViewModel> ComponentContext.retainedViewModel(
    modelClass: KClass<VM>,
    key: String = modelClass.toString(),
    build: () -> VM,
): VM {
    val holder = instanceKeeper.getOrCreate(key, ::RetainedViewModelStore)
    return ViewModelProvider.create(holder.store, SingleViewModelFactory(build))[modelClass]
}

/** Reified convenience form: `retainedViewModel { TransactionsViewModel(...) }`. */
inline fun <reified VM : ViewModel> ComponentContext.retainedViewModel(
    key: String = VM::class.toString(),
    noinline build: () -> VM,
): VM = retainedViewModel(VM::class, key, build)
