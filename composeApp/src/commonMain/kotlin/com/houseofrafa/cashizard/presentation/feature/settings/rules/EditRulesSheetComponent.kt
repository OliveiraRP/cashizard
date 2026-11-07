package com.houseofrafa.cashizard.presentation.feature.settings.rules

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.houseofrafa.cashizard.presentation.arch.koinViewModel

/** Hosts the "Automatic Categorization Rules" sheet. Navigation only: state lives in [viewModel]. */
class EditRulesSheetComponent(
    componentContext: ComponentContext,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<EditRulesSheetConfig>()

    val viewModel: EditRulesViewModel = koinViewModel()

    val stack: Value<ChildStack<*, EditRulesSheetConfig>> = childStack(
        source = navigation,
        serializer = EditRulesSheetConfig.serializer(),
        initialConfiguration = EditRulesSheetConfig.Root,
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

    fun onCloseClick() = viewModel.onRequestClose()

    fun onBackClick() = navigation.pop()

    fun onDismissed() = onDismiss()

    fun onNewRule() {
        viewModel.startNewRule()
        navigation.pushNew(EditRulesSheetConfig.RuleForm)
    }

    fun onEditRule(ruleId: String) {
        if (viewModel.startEditingRule(ruleId)) {
            navigation.pushNew(EditRulesSheetConfig.RuleForm)
        }
    }

    fun onPickCategory() = navigation.pushNew(EditRulesSheetConfig.CategoryPicker)

    fun onPickWallet() = navigation.pushNew(EditRulesSheetConfig.WalletPicker)

    fun onCategorySelected(categoryId: String) {
        viewModel.onCategorySelected(categoryId)
        navigation.pop()
    }

    fun onWalletSelected(walletId: String) {
        viewModel.onWalletSelected(walletId)
        navigation.pop()
    }

    /** The rule form finished saving. */
    fun onFormSaved() = navigation.pop()
}
