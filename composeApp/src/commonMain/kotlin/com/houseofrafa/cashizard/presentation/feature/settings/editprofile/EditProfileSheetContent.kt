package com.houseofrafa.cashizard.presentation.feature.settings.editprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetTextRow
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetCloseButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetScaffold
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

@Composable
fun EditProfileSheetContent(
    component: EditProfileSheetComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.viewModel.state.collectAsState()
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    SheetScaffold(
        title = "Edit Name",
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
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            SectionHeader(title = "Name", style = CashizardTheme.typography.sectionLabelSmall)
            InsetTextRow(
                value = state.name,
                onValueChange = component.viewModel::onNameChange,
                enabled = !state.saving,
                modifier = Modifier.padding(horizontal = dimens.listPadding),
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage.orEmpty(),
                    style = CashizardTheme.typography.footnote,
                    color = colors.errorText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.screenPadding, vertical = dimens.space12),
                )
            }
        }
    }
}
