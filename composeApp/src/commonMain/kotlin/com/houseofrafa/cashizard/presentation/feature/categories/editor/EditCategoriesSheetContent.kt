package com.houseofrafa.cashizard.presentation.feature.categories.editor

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
import com.composables.icons.lucide.Shapes
import com.composables.icons.lucide.Tag
import com.houseofrafa.cashizard.presentation.common.IconPickerGrid
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.PopoverMenu
import com.houseofrafa.cashizard.presentation.designsystem.components.PopoverMenuItem
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetBackButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetCloseButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetScaffold
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.feature.categories.CategoryFormScreen
import com.houseofrafa.cashizard.presentation.feature.categories.EditCategoriesScreen
import com.houseofrafa.cashizard.presentation.feature.categories.GroupFormScreen
import com.houseofrafa.cashizard.presentation.feature.categories.GroupPickerScreen

/**
 * Renders the standalone Edit-Categories sheet's inner stack with an iOS push.
 * The root gets close + a "⋯" menu for new category/group; the forms get their
 * own save; the rest get a back chevron.
 */
@Composable
fun EditCategoriesSheetContent(
    component: EditCategoriesSheetComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.stack.subscribeAsState()
    val state by component.viewModel.state.collectAsState()

    LaunchedEffect(component) {
        component.viewModel.events.collect { event ->
            when (event) {
                EditCategoriesEvent.FormSaved -> component.onFormSaved()
            }
        }
    }
    val isRoot = stack.backStack.isEmpty()
    val active = stack.active.instance

    var menuOpen by remember { mutableStateOf(false) }
    LaunchedEffect(active) {
        if (active != EditCategoriesSheetConfig.Root) menuOpen = false
    }

    val title = when (active) {
        EditCategoriesSheetConfig.Root -> "Edit Categories"
        EditCategoriesSheetConfig.GroupForm ->
            if (state.groupForm?.isEditing == true) "Edit Category Group" else "New Category Group"
        EditCategoriesSheetConfig.CategoryForm ->
            if (state.categoryForm?.isEditing == true) "Edit Category" else "New Category"
        EditCategoriesSheetConfig.GroupPicker -> "Choose Group"
        EditCategoriesSheetConfig.IconPicker -> "Choose Icon"
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
            trailing = sheetTrailing(active = active, state = state, component = component, onMenuClick = { menuOpen = true }),
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
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = MENU_TOP, end = CashizardTheme.dimens.listPadding),
            )
        }
    }
}

@Composable
private fun SheetChild(
    component: EditCategoriesSheetComponent,
    state: EditCategoriesUiState,
    child: EditCategoriesSheetConfig,
) {
    when (child) {
        EditCategoriesSheetConfig.Root ->
            EditCategoriesScreen(
                groups = state.categoryGroups,
                onEditGroup = component::onEditGroup,
                onEditCategory = component::onEditCategory,
            )

        EditCategoriesSheetConfig.GroupForm ->
            state.groupForm?.let { form ->
                GroupFormScreen(
                    form = form,
                    onNameChange = component.viewModel::onGroupNameChange,
                    onTypeChange = component.viewModel::onGroupTypeChange,
                    onColorChange = component.viewModel::onGroupColorChange,
                )
            }

        EditCategoriesSheetConfig.CategoryForm ->
            state.categoryForm?.let { form ->
                CategoryFormScreen(
                    form = form,
                    group = state.group(form.groupId),
                    onNameChange = component.viewModel::onCategoryNameChange,
                    onPickGroup = component::onPickCategoryGroup,
                    onPickIcon = component::onPickCategoryIcon,
                    onExcludeChange = component.viewModel::onCategoryExcludeChange,
                )
            }

        EditCategoriesSheetConfig.GroupPicker ->
            state.categoryForm?.let { form ->
                GroupPickerScreen(
                    form = form,
                    groups = state.groups,
                    onGroupSelected = component::onCategoryGroupSelected,
                )
            }

        EditCategoriesSheetConfig.IconPicker ->
            state.categoryForm?.let { form ->
                IconPickerGrid(
                    selected = form.icon,
                    onIconSelected = component::onCategoryIconSelected,
                    color = state.group(form.groupId)
                        ?.let { CategoryColors.parse(it.color) }
                        ?: CashizardTheme.colors.accent,
                )
            }
    }
}

private fun sheetTrailing(
    active: EditCategoriesSheetConfig,
    state: EditCategoriesUiState,
    component: EditCategoriesSheetComponent,
    onMenuClick: () -> Unit,
): (@Composable () -> Unit)? = when (active) {
    EditCategoriesSheetConfig.Root -> {
        {
            CircleIconButton(
                icon = Lucide.Ellipsis,
                onClick = onMenuClick,
                contentDescription = "Category options",
                iconSize = 18.dp,
            )
        }
    }

    EditCategoriesSheetConfig.GroupForm -> {
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

    EditCategoriesSheetConfig.CategoryForm -> {
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
