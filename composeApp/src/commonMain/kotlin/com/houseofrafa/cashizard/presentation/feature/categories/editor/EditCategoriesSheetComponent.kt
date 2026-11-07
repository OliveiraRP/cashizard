package com.houseofrafa.cashizard.presentation.feature.categories.editor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.houseofrafa.cashizard.presentation.arch.koinViewModel

/** Hosts the standalone Edit-Categories sheet. Navigation only: state lives in [viewModel]. */
class EditCategoriesSheetComponent(
    componentContext: ComponentContext,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<EditCategoriesSheetConfig>()

    val viewModel: EditCategoriesViewModel = koinViewModel()

    val stack: Value<ChildStack<*, EditCategoriesSheetConfig>> = childStack(
        source = navigation,
        serializer = EditCategoriesSheetConfig.serializer(),
        initialConfiguration = EditCategoriesSheetConfig.Root,
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

    fun onPickCategoryGroup() = navigation.pushNew(EditCategoriesSheetConfig.GroupPicker)

    fun onPickCategoryIcon() = navigation.pushNew(EditCategoriesSheetConfig.IconPicker)

    fun onNewCategoryGroup() {
        viewModel.startNewGroup()
        navigation.pushNew(EditCategoriesSheetConfig.GroupForm)
    }

    fun onNewCategory() {
        viewModel.startNewCategory()
        navigation.pushNew(EditCategoriesSheetConfig.CategoryForm)
    }

    fun onEditGroup(groupId: String) {
        if (viewModel.startEditingGroup(groupId)) {
            navigation.pushNew(EditCategoriesSheetConfig.GroupForm)
        }
    }

    fun onEditCategory(categoryId: String) {
        if (viewModel.startEditingCategory(categoryId)) {
            navigation.pushNew(EditCategoriesSheetConfig.CategoryForm)
        }
    }

    fun onCategoryGroupSelected(groupId: String) {
        viewModel.onCategoryGroupChange(groupId)
        navigation.pop()
    }

    fun onCategoryIconSelected(icon: String) {
        viewModel.onCategoryIconChange(icon)
        navigation.pop()
    }

    /** A category or group form finished saving. */
    fun onFormSaved() = navigation.pop()
}
