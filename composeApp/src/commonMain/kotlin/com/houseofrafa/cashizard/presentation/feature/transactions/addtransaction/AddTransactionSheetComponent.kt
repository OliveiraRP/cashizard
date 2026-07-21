package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.houseofrafa.cashizard.presentation.arch.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Hosts the add-transaction sheet's internal push stack.
 *
 * Navigation only: picking a value writes it to [viewModel] and then pops, so
 * the pickers never have to hand results back up the stack. Saving a category
 * or group form is asynchronous, so the pop for those is driven by
 * [AddTransactionEvent.FormSaved] which the content collects.
 */
class AddTransactionSheetComponent(
    componentContext: ComponentContext,
    /** When set, the sheet edits this transaction instead of creating a new one. */
    editing: EditingTransaction? = null,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<AddTransactionSheetConfig>()

    val viewModel: AddTransactionViewModel = koinViewModel { parametersOf(editing) }

    val stack: Value<ChildStack<*, AddTransactionSheetConfig>> = childStack(
        source = navigation,
        serializer = AddTransactionSheetConfig.serializer(),
        initialConfiguration = AddTransactionSheetConfig.NewTransaction,
        handleBackButton = false,
        childFactory = { config, _ -> config },
    )

    init {
        backHandler.register(
            BackCallback {
                if (stack.value.backStack.isEmpty()) viewModel.onRequestClose() else navigation.pop()
            },
        )
    }

    // ---- navigation -------------------------------------------------------

    fun onCloseClick() = viewModel.onRequestClose()

    fun onBackClick() = navigation.pop()

    fun onDismissed() = onDismiss()

    fun onPickCategory() = navigation.pushNew(AddTransactionSheetConfig.CategoryPicker)

    fun onPickWallet(target: WalletTarget) =
        navigation.pushNew(AddTransactionSheetConfig.WalletPicker(target))

    fun onEditCategories() = navigation.pushNew(AddTransactionSheetConfig.EditCategories)

    fun onPickCategoryGroup() = navigation.pushNew(AddTransactionSheetConfig.GroupPicker)

    fun onPickCategoryIcon() = navigation.pushNew(AddTransactionSheetConfig.IconPicker)

    // ---- open a form, then push it ----------------------------------------

    fun onNewCategoryGroup() {
        viewModel.startNewGroup()
        navigation.pushNew(AddTransactionSheetConfig.GroupForm)
    }

    fun onNewCategory() {
        viewModel.startNewCategory()
        navigation.pushNew(AddTransactionSheetConfig.CategoryForm)
    }

    fun onEditGroup(groupId: String) {
        if (viewModel.startEditingGroup(groupId)) {
            navigation.pushNew(AddTransactionSheetConfig.GroupForm)
        }
    }

    fun onEditCategory(categoryId: String) {
        if (viewModel.startEditingCategory(categoryId)) {
            navigation.pushNew(AddTransactionSheetConfig.CategoryForm)
        }
    }

    // ---- pick a value, then pop -------------------------------------------

    fun onCategoryGroupSelected(groupId: String) {
        viewModel.onCategoryGroupChange(groupId)
        navigation.pop()
    }

    fun onCategoryIconSelected(icon: String) {
        viewModel.onCategoryIconChange(icon)
        navigation.pop()
    }

    fun onCategorySelected(categoryId: String) {
        viewModel.onCategorySelected(categoryId)
        navigation.pop()
    }

    fun onWalletSelected(target: WalletTarget, walletId: String) {
        viewModel.onWalletSelected(target, walletId)
        navigation.pop()
    }

    /** A category or group form finished saving. */
    fun onFormSaved() = navigation.pop()
}
