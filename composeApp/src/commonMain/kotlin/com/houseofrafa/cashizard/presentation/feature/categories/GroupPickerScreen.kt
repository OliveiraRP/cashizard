package com.houseofrafa.cashizard.presentation.feature.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * Picks the group a category belongs to. Only groups of
 * [CategoryFormUiState.restrictToType] are offered: moving a category across
 * types would strand its transactions against the schema's category/type check.
 */
@Composable
fun GroupPickerScreen(
    form: CategoryFormUiState,
    groups: List<CategoryGroup>,
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = CashizardTheme.dimens
    val selectable = groups.filter { it.type == form.restrictToType }

    if (selectable.isEmpty()) {
        Hint("No ${form.restrictToType.label.lowercase()} groups yet.", modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimens.listPadding),
    ) {
        FormCard(
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + 10.dp + dimens.space12,
            rows = selectable.map { group ->
                {
                    GroupRow(
                        group = group,
                        trailing = { SelectionMark(group.id == form.groupId) },
                        onClick = { onGroupSelected(group.id) },
                    )
                }
            },
        )
    }
}
