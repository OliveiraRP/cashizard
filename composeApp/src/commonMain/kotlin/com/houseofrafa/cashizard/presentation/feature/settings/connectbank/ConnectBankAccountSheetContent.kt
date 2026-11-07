package com.houseofrafa.cashizard.presentation.feature.settings.connectbank

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.domain.model.Account
import com.houseofrafa.cashizard.domain.model.AspspOption
import com.houseofrafa.cashizard.presentation.common.PickerRow
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.PrimaryButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SearchField
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetBackButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetCloseButton
import com.houseofrafa.cashizard.presentation.designsystem.components.SheetScaffold
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.iconFor
import com.houseofrafa.cashizard.presentation.feature.categories.SelectionMark

@Composable
fun ConnectBankAccountSheetContent(
    component: ConnectBankAccountSheetComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(component) {
        component.viewModel.events.collect { event ->
            when (event) {
                is ConnectBankAccountEvent.OpenUrl -> uriHandler.openUri(event.url)
            }
        }
    }

    val title = when (state.step) {
        ConnectStep.PickAccount -> "Connect Bank Account"
        ConnectStep.PickBank -> "Choose Your Bank"
        ConnectStep.WaitingForBank -> "Finish In Browser"
    }

    SheetScaffold(
        title = title,
        modifier = modifier,
        leading = {
            if (state.step == ConnectStep.PickAccount) {
                SheetCloseButton(onClick = component.viewModel::onRequestClose)
            } else {
                SheetBackButton(onClick = component.viewModel::onBack)
            }
        },
        trailing = {
            when (state.step) {
                ConnectStep.PickAccount -> CircleIconButton(
                    icon = Lucide.ArrowRight,
                    onClick = component.viewModel::onContinueToBankPicker,
                    contentDescription = "Continue",
                    style = CircleButtonStyle.Accent,
                    enabled = state.canContinueToBankPicker,
                )

                else -> Unit
            }
        },
    ) {
        when (state.step) {
            ConnectStep.PickAccount -> AccountStep(state = state, component = component)
            ConnectStep.PickBank -> BankStep(state = state, component = component)
            ConnectStep.WaitingForBank -> WaitingStep(state = state, component = component)
        }
    }
}

@Composable
private fun AccountStep(state: ConnectBankAccountUiState, component: ConnectBankAccountSheetComponent) {
    val dimens = CashizardTheme.dimens

    if (state.loading) {
        LoadingBox()
        return
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        SectionHeader(title = "Account", style = CashizardTheme.typography.sectionLabelSmall)
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = state.accounts.map { account ->
                {
                    AccountOptionRow(
                        account = account,
                        selected = account.id == state.selectedAccountId,
                        onClick = { component.viewModel.onAccountSelected(account.id) },
                    )
                }
            },
        )
        ErrorText(state.errorMessage)
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun AccountOptionRow(account: Account, selected: Boolean, onClick: () -> Unit) {
    CashListRow(
        title = account.name,
        onClick = onClick,
        leading = {
            IconDisc(
                icon = iconFor(account.icon),
                style = IconDiscStyle.Neutral,
                size = CashizardTheme.dimens.iconDiscWallet,
                iconSize = CashizardTheme.dimens.space16,
            )
        },
        trailing = { SelectionMark(selected) },
    )
}

@Composable
private fun BankStep(state: ConnectBankAccountUiState, component: ConnectBankAccountSheetComponent) {
    val dimens = CashizardTheme.dimens

    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(
            value = state.bankQuery,
            onValueChange = component.viewModel::onBankQueryChange,
            placeholder = "Search banks",
            modifier = Modifier.padding(horizontal = dimens.listPadding, vertical = dimens.space12),
        )

        if (state.loadingBanks) {
            LoadingBox()
        } else {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                FormCard(
                    modifier = Modifier.padding(horizontal = dimens.listPadding),
                    cornerRadius = dimens.radiusControl,
                    separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
                    rows = state.filteredAspsps.map { aspsp ->
                        {
                            BankOptionRow(
                                aspsp = aspsp,
                                selected = aspsp == state.selectedAspsp,
                                onClick = { component.viewModel.onAspspSelected(aspsp) },
                            )
                        }
                    },
                )
                ErrorText(state.errorMessage)
                Spacer(Modifier.height(dimens.space16))
            }

            PrimaryButton(
                text = "Continue",
                onClick = component.viewModel::onStartAuth,
                enabled = state.canStartAuth,
                loading = state.startingAuth,
                modifier = Modifier
                    .padding(horizontal = dimens.listPadding, vertical = dimens.space12)
                    .navigationBarsPadding(),
            )
        }
    }
}

@Composable
private fun BankOptionRow(aspsp: AspspOption, selected: Boolean, onClick: () -> Unit) {
    CashListRow(
        title = aspsp.name,
        subtitle = aspsp.country,
        onClick = onClick,
        leading = {
            IconDisc(
                icon = Lucide.Landmark,
                style = IconDiscStyle.Neutral,
                size = CashizardTheme.dimens.iconDiscWallet,
                iconSize = CashizardTheme.dimens.space16,
            )
        },
        trailing = { SelectionMark(selected) },
    )
}

@Composable
private fun WaitingStep(state: ConnectBankAccountUiState, component: ConnectBankAccountSheetComponent) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = dimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(dimens.space32))
        Text(
            text = "Finish signing in with ${state.selectedAspsp?.name ?: "your bank"} in the browser that just opened, then come back here.",
            style = CashizardTheme.typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(dimens.space20))
        PrimaryButton(
            text = "Done",
            onClick = component.viewModel::onCheckLinked,
            loading = state.checkingLink,
            modifier = Modifier.fillMaxWidth(),
        )
        ErrorText(state.errorMessage)
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = CashizardTheme.colors.accent,
            strokeWidth = 2.dp,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Composable
private fun ErrorText(message: String?) {
    if (message == null) return
    val dimens = CashizardTheme.dimens
    Text(
        text = message,
        style = CashizardTheme.typography.footnote,
        color = CashizardTheme.colors.errorText,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.screenPadding, vertical = dimens.space12),
    )
}
