package com.houseofrafa.cashizard.presentation.feature.analytics.filtercategories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetCloseButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetScaffold
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import com.houseofrafa.cashizard.presentation.feature.categories.GroupRow
import com.houseofrafa.cashizard.presentation.feature.categories.SelectionMark

/**
 * The bulk "Filter categories" sheet: the Edit Categories layout, but every
 * category row carries a checkmark. Checked means it counts in analytics.
 */
@Composable
fun FilterCategoriesSheetContent(
    component: FilterCategoriesSheetComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.viewModel.state.collectAsState()
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    SheetScaffold(
        title = "Filter Categories",
        modifier = modifier,
        leading = { SheetCloseButton(onClick = component.viewModel::onRequestClose) },
        trailing = {
            CircleIconButton(
                icon = Lucide.Check,
                onClick = component.viewModel::onSave,
                contentDescription = "Save",
                style = CircleButtonStyle.Accent,
                enabled = state.canSave,
            )
        },
    ) {
        when {
            state.loading -> Centered {
                CircularProgressIndicator(
                    color = colors.accent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(28.dp),
                )
            }

            state.errorMessage != null -> Centered {
                Text(
                    text = state.errorMessage.orEmpty(),
                    style = CashizardTheme.typography.footnote,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                )
            }

            state.groups.isEmpty() -> Centered {
                Text(
                    text = "No categories yet.",
                    style = CashizardTheme.typography.footnote,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = dimens.listPadding,
                        end = dimens.listPadding,
                        top = dimens.space16,
                        bottom = dimens.space32,
                    ),
                verticalArrangement = Arrangement.spacedBy(dimens.space12),
            ) {
                state.groups.forEach { entry ->
                    FormCard(
                        cornerRadius = dimens.radiusControl,
                        separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
                        rows = buildList {
                            add {
                                // Inert header: no chevron, no navigation — the
                                // group only groups its category rows here.
                                GroupRow(
                                    group = entry.group,
                                    emphasized = true,
                                    onClick = {},
                                    trailing = {},
                                )
                            }
                            entry.categories.forEach { category ->
                                add {
                                    CategoryCheckRow(
                                        category = category,
                                        groupColorHex = entry.group.color,
                                        checked = state.isChecked(category.id),
                                        onToggle = { component.viewModel.onToggle(category.id) },
                                    )
                                }
                            }
                        },
                    )
                }
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

/** A category as its group-colored disc, its name, and a trailing checkmark. */
@Composable
private fun CategoryCheckRow(
    category: Category,
    groupColorHex: String,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimens.rowHeightCompact)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle,
            )
            .padding(horizontal = dimens.space16, vertical = dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        IconDisc(
            icon = iconFor(category.icon),
            color = CategoryColors.parse(groupColorHex),
            style = IconDiscStyle.Solid,
            size = dimens.iconDiscWallet,
            iconSize = dimens.space16,
        )
        Text(
            text = category.name,
            style = CashizardTheme.typography.bodyLarge,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        SelectionMark(selected = checked)
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(CashizardTheme.dimens.space32),
        contentAlignment = Alignment.Center,
    ) { content() }
}
