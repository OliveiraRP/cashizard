package com.houseofrafa.cashizard.presentation.feature.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Shapes
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.presentation.common.PickerRow
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetTextRow
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor

/**
 * "New / Edit Category": name, owning group and icon. A category has no color
 * of its own — it wears its group's — so the design offers none here.
 */
@Composable
fun CategoryFormScreen(
    form: CategoryFormUiState,
    group: CategoryGroup?,
    onNameChange: (String) -> Unit,
    onPickGroup: () -> Unit,
    onPickIcon: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = CashizardTheme.dimens
    // Until a group is chosen the discs have no group color to wear, so they
    // fall back to the accent rather than to an arbitrary palette entry.
    val tint = group?.let { CategoryColors.parse(it.color) } ?: CashizardTheme.colors.accent

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        SectionHeader(title = "Name", style = CashizardTheme.typography.sectionLabelSmall)
        InsetTextRow(
            value = form.name,
            onValueChange = onNameChange,
            modifier = Modifier.padding(horizontal = dimens.listPadding),
        )

        SectionHeader(
            title = "Group & icon",
            style = CashizardTheme.typography.sectionLabelSmall,
        )
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = listOf(
                {
                    PickerRow(
                        label = "Group",
                        value = group?.name ?: "Choose",
                        onClick = onPickGroup,
                        leading = {
                            IconDisc(
                                icon = Lucide.Shapes,
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
                        label = "Icon",
                        // The disc already shows the choice; the raw registry
                        // name means nothing to the reader.
                        value = null,
                        onClick = onPickIcon,
                        leading = {
                            IconDisc(
                                icon = iconFor(form.icon),
                                color = tint,
                                style = IconDiscStyle.Solid,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                    )
                },
            ),
        )

        if (group == null) {
            Hint(
                "There are no ${form.restrictToType.label.lowercase()} groups yet — " +
                    "create one first, then file this category under it.",
            )
        }

        FormError(form.errorMessage)
        Spacer(Modifier.height(dimens.space32).navigationBarsPadding())
    }
}
