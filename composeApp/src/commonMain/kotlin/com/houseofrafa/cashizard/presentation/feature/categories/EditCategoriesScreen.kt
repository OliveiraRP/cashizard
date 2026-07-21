package com.houseofrafa.cashizard.presentation.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.houseofrafa.cashizard.domain.model.CategoryGroupWithCategories
import com.houseofrafa.cashizard.presentation.common.PickerRow
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor

/**
 * "Edit Categories": every group as a card, its name row on top and its
 * categories beneath. Tapping either opens the matching form.
 */
@Composable
fun EditCategoriesScreen(
    groups: List<CategoryGroupWithCategories>,
    onEditGroup: (String) -> Unit,
    onEditCategory: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = CashizardTheme.dimens

    if (groups.isEmpty()) {
        Hint("No categories yet. Add a group to get started.", modifier)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = dimens.listPadding,
                end = dimens.listPadding,
                top = dimens.space20,
                bottom = dimens.space32,
            ),
        verticalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        groups.forEach { entry ->
            FormCard(
                cornerRadius = dimens.radiusControl,
                separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
                rows = buildList {
                    add {
                        GroupRow(
                            group = entry.group,
                            // The label disambiguates the group's own row from
                            // the category rows stacked under it.
                            value = "Group",
                            emphasized = true,
                            onClick = { onEditGroup(entry.group.id) },
                        )
                    }
                    entry.categories.forEach { category ->
                        add {
                            PickerRow(
                                label = category.name,
                                onClick = { onEditCategory(category.id) },
                                leading = {
                                    IconDisc(
                                        icon = iconFor(category.icon),
                                        color = CategoryColors.parse(entry.group.color),
                                        style = IconDiscStyle.Solid,
                                        size = dimens.iconDiscWallet,
                                        iconSize = dimens.space16,
                                    )
                                },
                            )
                        }
                    }
                },
            )
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}
