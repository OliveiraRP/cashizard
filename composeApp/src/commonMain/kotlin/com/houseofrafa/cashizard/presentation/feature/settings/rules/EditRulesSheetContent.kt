package com.houseofrafa.cashizard.presentation.feature.settings.rules

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Trash2
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.presentation.common.PickerRow
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetTextRow
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.components.SegmentedControl
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetBackButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetCloseButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetScaffold
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import com.houseofrafa.cashizard.presentation.feature.categories.FormError
import com.houseofrafa.cashizard.presentation.feature.categories.Hint
import com.houseofrafa.cashizard.presentation.feature.categories.SelectionMark
import com.houseofrafa.cashizard.presentation.feature.categories.groupTypeOptions
import com.houseofrafa.cashizard.presentation.feature.categories.label

@Composable
fun EditRulesSheetContent(
    component: EditRulesSheetComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.stack.subscribeAsState()
    val state by component.viewModel.state.collectAsState()

    LaunchedEffect(component) {
        component.viewModel.events.collect { event ->
            when (event) {
                EditRulesEvent.FormSaved -> component.onFormSaved()
            }
        }
    }

    val isRoot = stack.backStack.isEmpty()
    val active = stack.active.instance
    val title = when (active) {
        EditRulesSheetConfig.Root -> "Automatic Rules"
        EditRulesSheetConfig.RuleForm -> if (state.ruleForm?.isEditing == true) "Edit Rule" else "New Rule"
        EditRulesSheetConfig.CategoryPicker -> "Choose Category"
        EditRulesSheetConfig.WalletPicker -> "Choose Wallet"
    }

    SheetScaffold(
        title = title,
        modifier = modifier,
        leading = {
            if (isRoot) {
                SheetCloseButton(onClick = component::onCloseClick)
            } else {
                SheetBackButton(onClick = component::onBackClick)
            }
        },
        trailing = {
            when (active) {
                EditRulesSheetConfig.Root -> CircleIconButton(
                    icon = Lucide.Plus,
                    onClick = component::onNewRule,
                    contentDescription = "New rule",
                    style = CircleButtonStyle.Accent,
                )

                EditRulesSheetConfig.RuleForm -> CircleIconButton(
                    icon = Lucide.Check,
                    onClick = component.viewModel::onSaveRule,
                    contentDescription = "Save rule",
                    style = CircleButtonStyle.Accent,
                    enabled = state.ruleForm?.canSave == true,
                )

                else -> Unit
            }
        },
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
}

@Composable
private fun SheetChild(
    component: EditRulesSheetComponent,
    state: EditRulesUiState,
    child: EditRulesSheetConfig,
) {
    when (child) {
        EditRulesSheetConfig.Root -> RulesListScreen(state = state, component = component)

        EditRulesSheetConfig.RuleForm ->
            state.ruleForm?.let { form -> RuleFormScreen(form = form, state = state, component = component) }

        EditRulesSheetConfig.CategoryPicker ->
            state.ruleForm?.let { form -> CategoryPickerForRule(form = form, state = state, component = component) }

        EditRulesSheetConfig.WalletPicker ->
            state.ruleForm?.let { form -> WalletPickerForRule(form = form, state = state, component = component) }
    }
}

@Composable
private fun RulesListScreen(state: EditRulesUiState, component: EditRulesSheetComponent) {
    val dimens = CashizardTheme.dimens

    if (state.rules.isEmpty()) {
        Hint("No rules yet. Categorize an uncategorized bank transaction and choose \"Always categorize this way\" to create one.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = dimens.space20, bottom = dimens.space32),
    ) {
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = state.rules.map { row ->
                {
                    CashListRow(
                        title = row.categoryName,
                        subtitle = "\"${row.matchText}\" → ${row.walletName}",
                        minHeight = dimens.rowHeight,
                        onClick = { component.onEditRule(row.ruleId) },
                        leading = {
                            IconDisc(
                                icon = iconFor(state.category(row.categoryId)?.icon),
                                color = state.groupOf(row.categoryId)?.color?.let { CategoryColors.parse(it) }
                                    ?: CashizardTheme.colors.accent,
                                style = IconDiscStyle.Solid,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                        trailing = {
                            CircleIconButton(
                                icon = Lucide.Trash2,
                                onClick = { component.viewModel.onDeleteRule(row.ruleId) },
                                contentDescription = "Delete rule",
                                style = CircleButtonStyle.Neutral,
                                size = 30.dp,
                                iconSize = 15.dp,
                            )
                        },
                    )
                }
            },
        )
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun RuleFormScreen(
    form: RuleFormUiState,
    state: EditRulesUiState,
    component: EditRulesSheetComponent,
) {
    val dimens = CashizardTheme.dimens
    val category = state.category(form.categoryId)
    val wallet = state.wallet(form.walletId)
    val tint = category?.let { state.groupOf(it.id)?.color }?.let { CategoryColors.parse(it) }
        ?: CashizardTheme.colors.accent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        SectionHeader(title = "Match Text", style = CashizardTheme.typography.sectionLabelSmall)
        InsetTextRow(
            value = form.matchText,
            onValueChange = component.viewModel::onMatchTextChange,
            placeholder = "e.g. uber eats",
            modifier = Modifier.padding(horizontal = dimens.listPadding),
        )

        SectionHeader(title = "Type", style = CashizardTheme.typography.sectionLabelSmall)
        SegmentedControl(
            options = groupTypeOptions.map { it.second },
            selectedIndex = groupTypeOptions.indexOfFirst { it.first == form.type },
            onSelect = { component.viewModel.onTypeChange(groupTypeOptions[it].first) },
            modifier = Modifier.padding(horizontal = dimens.listPadding),
        )

        SectionHeader(title = "Category & Wallet", style = CashizardTheme.typography.sectionLabelSmall)
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = listOf(
                {
                    PickerRow(
                        label = "Category",
                        value = category?.name ?: "Choose",
                        onClick = component::onPickCategory,
                        leading = {
                            IconDisc(
                                icon = iconFor(category?.icon ?: "tag"),
                                color = tint,
                                style = IconDiscStyle.Solid,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                    )
                },
                {
                    PickerRow(
                        label = "Wallet",
                        value = wallet?.name ?: "Choose",
                        onClick = component::onPickWallet,
                        leading = {
                            IconDisc(
                                icon = iconFor(wallet?.icon ?: "wallet"),
                                style = IconDiscStyle.Neutral,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                    )
                },
            ),
        )

        FormError(form.errorMessage)

        Spacer(Modifier.height(dimens.space32).navigationBarsPadding())
    }
}

@Composable
private fun CategoryPickerForRule(
    form: RuleFormUiState,
    state: EditRulesUiState,
    component: EditRulesSheetComponent,
) {
    val dimens = CashizardTheme.dimens
    val categories = state.categoriesForType(form.type)

    if (categories.isEmpty()) {
        Hint("No ${form.type.label.lowercase()} categories yet.")
        return
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(dimens.listPadding)) {
        FormCard(
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = categories.map { category ->
                {
                    CashListRow(
                        title = category.name,
                        minHeight = dimens.rowHeight,
                        onClick = { component.onCategorySelected(category.id) },
                        leading = {
                            IconDisc(
                                icon = iconFor(category.icon),
                                color = state.groupOf(category.id)?.color?.let { CategoryColors.parse(it) }
                                    ?: CashizardTheme.colors.accent,
                                style = IconDiscStyle.Solid,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                        trailing = { SelectionMark(category.id == form.categoryId) },
                    )
                }
            },
        )
    }
}

@Composable
private fun WalletPickerForRule(
    form: RuleFormUiState,
    state: EditRulesUiState,
    component: EditRulesSheetComponent,
) {
    val dimens = CashizardTheme.dimens

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(dimens.listPadding)) {
        FormCard(
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = state.wallets.map { wallet ->
                {
                    CashListRow(
                        title = wallet.name,
                        minHeight = dimens.rowHeight,
                        onClick = { component.onWalletSelected(wallet.id) },
                        leading = {
                            IconDisc(
                                icon = iconFor(wallet.icon),
                                style = IconDiscStyle.Neutral,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                        trailing = { SelectionMark(wallet.id == form.walletId) },
                    )
                }
            },
        )
    }
}
