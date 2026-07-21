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
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetTextRow
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.components.SegmentedControl
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors

/**
 * "New / Edit Category Group": name, type and color. Transfer groups are fixed
 * gray by the schema's convention, so for them the palette is not shown at all.
 */
@Composable
fun GroupFormScreen(
    form: GroupFormUiState,
    onNameChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onColorChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = CashizardTheme.dimens

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

        if (form.picksType) {
            SectionHeader(title = "Type", style = CashizardTheme.typography.sectionLabelSmall)
            SegmentedControl(
                options = groupTypeOptions.map { it.second },
                selectedIndex = groupTypeOptions.indexOfFirst { it.first == form.type },
                onSelect = { onTypeChange(groupTypeOptions[it].first) },
                modifier = Modifier.padding(horizontal = dimens.listPadding),
            )
        }

        if (form.picksColor) {
            SectionHeader(title = "Color", style = CashizardTheme.typography.sectionLabelSmall)
            ColorPalette(
                colors = CategoryColors.paletteHexFor(form.type),
                selected = form.color,
                onSelect = onColorChange,
                modifier = Modifier.padding(horizontal = dimens.listPadding),
            )
        }

        FormError(form.errorMessage)
        Spacer(Modifier.height(dimens.space32).navigationBarsPadding())
    }
}
