package com.houseofrafa.cashizard.presentation.feature.wallets.addwallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wallet
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.presentation.common.IconPickerGrid
import com.houseofrafa.cashizard.presentation.common.PickerRow
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetRow
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetTextRow
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetBackButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetCloseButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetScaffold
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import com.houseofrafa.cashizard.presentation.feature.wallets.label
import com.houseofrafa.cashizard.presentation.feature.wallets.typeIcon
import com.houseofrafa.cashizard.presentation.feature.wallets.WalletSelectionMark
import com.houseofrafa.cashizard.presentation.feature.wallets.WalletTypePicker

@Composable
fun AddWalletSheetContent(
    component: AddWalletSheetComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.stack.subscribeAsState()
    val state by component.viewModel.state.collectAsState()
    val isRoot = stack.backStack.isEmpty()

    val title = when (stack.active.instance) {
        AddWalletSheetConfig.Form -> "New Wallet"
        AddWalletSheetConfig.IconPicker -> "Choose Icon"
        AddWalletSheetConfig.TypePicker -> "Wallet Type"
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
        trailing = if (isRoot) {
            {
                CircleIconButton(
                    icon = Lucide.Check,
                    onClick = component.viewModel::onSave,
                    contentDescription = "Save",
                    style = CircleButtonStyle.Accent,
                    enabled = state.canSave,
                )
            }
        } else {
            null
        },
    ) {
        Children(
            stack = component.stack,
            modifier = Modifier.fillMaxWidth().weight(1f),
            animation = stackAnimation(slide()),
        ) { created ->
            when (created.instance) {
                AddWalletSheetConfig.Form -> WalletForm(component, state)
                AddWalletSheetConfig.IconPicker -> IconPickerGrid(
                    selected = state.icon,
                    onIconSelected = component::onIconSelected,
                )
                AddWalletSheetConfig.TypePicker -> WalletTypePicker(
                    selected = state.type,
                    onSelect = component::onTypeSelected,
                )
            }
        }
    }
}

@Composable
private fun WalletForm(component: AddWalletSheetComponent, state: AddWalletUiState) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        SectionHeader(title = "Name", style = CashizardTheme.typography.sectionLabelSmall)
        InsetTextRow(
            value = state.name,
            onValueChange = component.viewModel::onNameChange,
            modifier = Modifier.padding(horizontal = dimens.listPadding),
        )

        SectionHeader(title = "Icon & type", style = CashizardTheme.typography.sectionLabelSmall)
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = listOf(
                {
                    PickerRow(
                        label = "Icon",
                        // The disc already shows the choice; the raw registry
                        // name means nothing to the reader.
                        value = null,
                        onClick = component::onPickIcon,
                        leading = {
                            IconDisc(
                                icon = iconFor(state.icon),
                                color = colors.accent,
                                style = IconDiscStyle.Solid,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                    )
                },
                {
                    PickerRow(
                        label = "Type",
                        value = state.type.label,
                        onClick = component::onPickType,
                        leading = {
                            IconDisc(
                                icon = state.type.typeIcon(),
                                style = IconDiscStyle.Neutral,
                                size = dimens.iconDiscWallet,
                                iconSize = dimens.space16,
                            )
                        },
                    )
                },
            ),
        )

        if (state.needsGoalAmount || state.needsAnnualBudget) {
            SectionHeader(
                title = if (state.needsGoalAmount) "Goal amount" else "Annual budget",
                style = CashizardTheme.typography.sectionLabelSmall,
            )
            InsetTextRow(
                value = state.typeAmount.text,
                onValueChange = component.viewModel::onTypeAmountChange,
                placeholder = "0,00",
                textStyle = CashizardTheme.typography.amount,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.padding(horizontal = dimens.listPadding),
            )
        }

        if (state.accounts.isNotEmpty()) {
            SectionHeader(
                title = "Linked account",
                style = CashizardTheme.typography.sectionLabelSmall,
            )
            FormCard(
                modifier = Modifier.padding(horizontal = dimens.listPadding),
                cornerRadius = dimens.radiusControl,
                rows = state.accounts.map { account ->
                    {
                        // Optional and deselectable: tapping the linked account
                        // again leaves the wallet standing on its own.
                        AccountRow(
                            label = account.name,
                            selected = state.accountId == account.id,
                            onClick = { component.viewModel.onAccountToggled(account.id) },
                        )
                    }
                },
            )
        }

        SectionHeader(
            title = "Starting balance",
            style = CashizardTheme.typography.sectionLabelSmall,
        )
        InsetTextRow(
            value = state.startingBalance.text,
            onValueChange = component.viewModel::onStartingBalanceChange,
            placeholder = "0,00",
            textStyle = CashizardTheme.typography.amount,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            trailing = {
                SignToggle(
                    negative = state.startingBalance.isNegative,
                    onClick = component.viewModel::onToggleStartingBalanceSign,
                )
            },
        )

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                style = CashizardTheme.typography.footnote,
                color = colors.errorText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.screenPadding, vertical = dimens.space12),
            )
        }

        Spacer(Modifier.height(dimens.space32).navigationBarsPadding())
    }
}

/**
 * Flips a balance between positive and negative. A wallet can legitimately open
 * overdrawn, and soft keyboards do not reliably offer a minus key.
 */
@Composable
private fun SignToggle(negative: Boolean, onClick: () -> Unit) {
    val colors = CashizardTheme.colors
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                if (negative) colors.negative.copy(alpha = 0.18f) else colors.fillControl,
                CircleShape,
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (negative) "−" else "+",
            style = CashizardTheme.typography.headline,
            color = if (negative) colors.negative else colors.textSecondary,
        )
    }
}

/** A row that is checked when linked and hollow when not. */
@Composable
private fun AccountRow(label: String, selected: Boolean, onClick: () -> Unit) {
    InsetRow(
        label = label,
        leadingIcon = Lucide.Landmark,
        onClick = onClick,
        trailing = { WalletSelectionMark(selected) },
    )
}
