package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Ellipsis
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.Shapes
import com.composables.icons.lucide.Tag
import com.houseofrafa.cashizard.presentation.common.IconPickerGrid
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.PopoverMenu
import com.houseofrafa.cashizard.presentation.designsystem.components.PopoverMenuItem
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetBackButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetCloseButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetScaffold
import com.houseofrafa.cashizard.presentation.feature.categories.CategoryFormScreen
import com.houseofrafa.cashizard.presentation.feature.categories.EditCategoriesScreen
import com.houseofrafa.cashizard.presentation.feature.categories.GroupFormScreen
import com.houseofrafa.cashizard.presentation.feature.categories.GroupPickerScreen

/**
 * Renders the sheet's inner stack with an iOS push. The header swaps per entry:
 * the root gets close + save, the category picker gets a ⋯ menu, the category
 * and group forms get their own save, and the rest get a back chevron.
 */
@Composable
fun AddTransactionSheetContent(
    component: AddTransactionSheetComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.stack.subscribeAsState()
    val state by component.viewModel.state.collectAsState()

    // Saving a form is asynchronous, so the pop is driven by the ViewModel
    // reporting success rather than by the button press.
    LaunchedEffect(component) {
        component.viewModel.events.collect { event ->
            when (event) {
                AddTransactionEvent.FormSaved -> component.onFormSaved()
            }
        }
    }
    val isRoot = stack.backStack.isEmpty()
    val active = stack.active.instance

    var menuOpen by remember { mutableStateOf(false) }
    // The menu belongs to the picker's header, so navigating anywhere else
    // must take it down with it rather than leave it floating.
    LaunchedEffect(active) {
        if (active != AddTransactionSheetConfig.CategoryPicker) menuOpen = false
    }

    val title = when (active) {
        AddTransactionSheetConfig.NewTransaction ->
            if (state.isEditing) "Edit Transaction" else "New Transaction"
        AddTransactionSheetConfig.CategoryPicker -> "Choose Category"
        is AddTransactionSheetConfig.WalletPicker -> "Choose Wallet"
        AddTransactionSheetConfig.GroupForm ->
            if (state.groupForm?.isEditing == true) "Edit Category Group" else "New Category Group"
        AddTransactionSheetConfig.CategoryForm ->
            if (state.categoryForm?.isEditing == true) "Edit Category" else "New Category"
        AddTransactionSheetConfig.GroupPicker -> "Choose Group"
        AddTransactionSheetConfig.IconPicker -> "Choose Icon"
        AddTransactionSheetConfig.EditCategories -> "Edit Categories"
    }

    Box(modifier) {
        SheetScaffold(
            title = title,
            leading = {
                if (isRoot) {
                    SheetCloseButton(onClick = component::onCloseClick)
                } else {
                    SheetBackButton(onClick = component::onBackClick)
                }
            },
            trailing = sheetTrailing(
                active = active,
                state = state,
                component = component,
                onMenuClick = { menuOpen = true },
            ),
        ) {
            if (state.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = CashizardTheme.colors.accent,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(32.dp),
                    )
                }
                return@SheetScaffold
            }

            Children(
                stack = component.stack,
                modifier = Modifier.fillMaxWidth().weight(1f),
                animation = stackAnimation(slide()),
            ) { created ->
                SheetChild(component = component, state = state, child = created.instance)
            }
        }

        if (menuOpen) {
            // A full-screen catcher closes the menu on any outside tap, which is
            // how iOS context menus behave.
            Box(
                modifier = Modifier.fillMaxSize().clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { menuOpen = false },
                ),
            )
            PopoverMenu(
                items = listOf(
                    PopoverMenuItem("New category", Lucide.Tag) {
                        menuOpen = false
                        component.onNewCategory()
                    },
                    PopoverMenuItem("New category group", Lucide.Shapes) {
                        menuOpen = false
                        component.onNewCategoryGroup()
                    },
                    PopoverMenuItem("Edit categories", Lucide.Pencil) {
                        menuOpen = false
                        component.onEditCategories()
                    },
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = MENU_TOP,
                        end = CashizardTheme.dimens.listPadding,
                    ),
            )
        }
    }
}

@Composable
private fun SheetChild(
    component: AddTransactionSheetComponent,
    state: AddTransactionUiState,
    child: AddTransactionSheetConfig,
) {
    when (child) {
        AddTransactionSheetConfig.NewTransaction ->
            NewTransactionScreen(component = component, state = state)

        AddTransactionSheetConfig.CategoryPicker ->
            CategoryPickerScreen(component = component, state = state)

        is AddTransactionSheetConfig.WalletPicker ->
            WalletPickerScreen(component = component, state = state, target = child.target)

        AddTransactionSheetConfig.GroupForm ->
            state.groupForm?.let { form ->
                GroupFormScreen(
                    form = form,
                    onNameChange = component.viewModel::onGroupNameChange,
                    onTypeChange = component.viewModel::onGroupTypeChange,
                    onColorChange = component.viewModel::onGroupColorChange,
                )
            }

        AddTransactionSheetConfig.CategoryForm ->
            state.categoryForm?.let { form ->
                CategoryFormScreen(
                    form = form,
                    group = state.group(form.groupId),
                    onNameChange = component.viewModel::onCategoryNameChange,
                    onPickGroup = component::onPickCategoryGroup,
                    onPickIcon = component::onPickCategoryIcon,
                )
            }

        AddTransactionSheetConfig.GroupPicker ->
            state.categoryForm?.let { form ->
                GroupPickerScreen(
                    form = form,
                    groups = state.groups,
                    onGroupSelected = component::onCategoryGroupSelected,
                )
            }

        AddTransactionSheetConfig.IconPicker ->
            state.categoryForm?.let { form ->
                IconPickerGrid(
                    selected = form.icon,
                    onIconSelected = component::onCategoryIconSelected,
                    color = state.group(form.groupId)
                        ?.let { CategoryColors.parse(it.color) }
                        ?: CashizardTheme.colors.accent,
                )
            }

        AddTransactionSheetConfig.EditCategories ->
            EditCategoriesScreen(
                groups = state.categoryGroups,
                onEditGroup = component::onEditGroup,
                onEditCategory = component::onEditCategory,
            )
    }
}

/** The header's trailing control, which differs per stack entry. */
private fun sheetTrailing(
    active: AddTransactionSheetConfig,
    state: AddTransactionUiState,
    component: AddTransactionSheetComponent,
    onMenuClick: () -> Unit,
): (@Composable () -> Unit)? = when (active) {
    AddTransactionSheetConfig.NewTransaction -> {
        {
            CircleIconButton(
                icon = Lucide.Check,
                onClick = component.viewModel::onSave,
                contentDescription = "Save",
                style = CircleButtonStyle.Accent,
                enabled = state.canSave,
            )
        }
    }

    AddTransactionSheetConfig.CategoryPicker -> {
        {
            CircleIconButton(
                icon = Lucide.Ellipsis,
                onClick = onMenuClick,
                contentDescription = "Category options",
                iconSize = 18.dp,
            )
        }
    }

    AddTransactionSheetConfig.GroupForm -> {
        {
            CircleIconButton(
                icon = Lucide.Check,
                onClick = component.viewModel::onSaveGroup,
                contentDescription = "Save group",
                style = CircleButtonStyle.Accent,
                enabled = state.groupForm?.canSave == true,
            )
        }
    }

    AddTransactionSheetConfig.CategoryForm -> {
        {
            CircleIconButton(
                icon = Lucide.Check,
                onClick = component.viewModel::onSaveCategory,
                contentDescription = "Save category",
                style = CircleButtonStyle.Accent,
                enabled = state.categoryForm?.canSave == true,
            )
        }
    }

    else -> null
}

/** Clears the sheet header so the menu hangs just under the ⋯ button. */
private val MENU_TOP = 60.dp
