package com.houseofrafa.cashizard.presentation.feature.transactions.addtransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.domain.model.Category
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.components.CategoryChip
import com.houseofrafa.cashizard.presentation.designsystem.components.SearchField
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor

/**
 * The full category list, grouped, searchable. Only groups matching the current
 * transaction type are shown — the schema rejects anything else.
 */
@Composable
fun CategoryPickerScreen(
    component: AddTransactionSheetComponent,
    state: AddTransactionUiState,
    modifier: Modifier = Modifier,
) {
    val dimens = CashizardTheme.dimens
    var query by remember { mutableStateOf("") }

    val groups = state.categoryGroups
        .filter { it.group.type == state.type }
        .map { entry ->
            entry.group to entry.categories.filter {
                query.isBlank() || it.name.contains(query, ignoreCase = true)
            }
        }
        .filter { (_, categories) -> categories.isNotEmpty() }

    Column(modifier = modifier.fillMaxWidth()) {
        SearchField(
            value = query,
            onValueChange = { query = it },
            placeholder = "Search categories",
            modifier = Modifier.padding(
                start = dimens.listPadding,
                end = dimens.listPadding,
                top = dimens.space20,
            ),
        )

        if (groups.isEmpty()) {
            EmptyCategories(hasQuery = query.isNotBlank())
            return@Column
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(COLUMNS),
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(
                start = dimens.listPadding,
                end = dimens.listPadding,
                top = dimens.space20,
                bottom = dimens.space32,
            ),
            horizontalArrangement = Arrangement.spacedBy(dimens.space8),
            verticalArrangement = Arrangement.spacedBy(dimens.space12),
        ) {
            groups.forEach { (group, categories) ->
                item(span = { GridItemSpan(COLUMNS) }) {
                    GroupHeader(group)
                }
                itemsIndexed(categories, key = { _, category -> category.id }) { _, category ->
                    CategoryChip(
                        label = category.name,
                        icon = iconFor(category.icon),
                        color = CategoryColors.parse(group.color),
                        selected = category.id == state.categoryId,
                        onClick = { component.onCategorySelected(category.id) },
                    )
                }
            }
        }
    }
}

/** A color dot and the group's name above its categories. */
@Composable
private fun GroupHeader(group: CategoryGroup) {
    val dimens = CashizardTheme.dimens
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = dimens.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(CategoryColors.parse(group.color), CircleShape),
        )
        Spacer(Modifier.width(dimens.space8))
        Text(
            text = group.name,
            style = CashizardTheme.typography.caption,
            color = CashizardTheme.colors.textTertiary,
        )
    }
}

@Composable
private fun EmptyCategories(hasQuery: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(CashizardTheme.dimens.space32),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (hasQuery) {
                "No categories match that search."
            } else {
                "No categories for this type yet."
            },
            style = CashizardTheme.typography.footnote,
            color = CashizardTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
        )
    }
}

private const val COLUMNS = 5
