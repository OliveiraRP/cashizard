package com.houseofrafa.cashizard.presentation.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.composables.icons.lucide.Landmark
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.Tag
import com.composables.icons.lucide.Unlink
import com.composables.icons.lucide.User
import com.houseofrafa.cashizard.presentation.common.PickerRow
import com.houseofrafa.cashizard.presentation.common.ScreenHeader
import com.houseofrafa.cashizard.presentation.designsystem.components.CashListRow
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleButtonStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.CircleIconButton
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDisc
import com.houseofrafa.cashizard.presentation.designsystem.components.IconDiscStyle
import com.houseofrafa.cashizard.presentation.designsystem.components.SectionHeader
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    spaceName: String?,
    onEditProfile: () -> Unit,
    onEditCategories: () -> Unit,
    onEditRules: () -> Unit,
    onConnectBankAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding(),
    ) {
        ScreenHeader(
            title = "Settings",
            spaceName = spaceName,
            modifier = Modifier.padding(top = dimens.space16),
        )

        when {
            state.loading -> CenteredBox {
                CircularProgressIndicator(
                    color = colors.accent,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(28.dp),
                )
            }

            state.errorMessage != null -> CenteredBox {
                Text(
                    text = state.errorMessage.orEmpty(),
                    style = CashizardTheme.typography.footnote,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                )
            }

            else -> SettingsContent(
                state = state,
                onEditProfile = onEditProfile,
                onEditCategories = onEditCategories,
                onEditRules = onEditRules,
                onConnectBankAccount = onConnectBankAccount,
                onUnlink = viewModel::onUnlinkBankAccount,
                onSignOut = viewModel::onSignOut,
            )
        }
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onEditProfile: () -> Unit,
    onEditCategories: () -> Unit,
    onEditRules: () -> Unit,
    onConnectBankAccount: () -> Unit,
    onUnlink: (String) -> Unit,
    onSignOut: () -> Unit,
) {
    val dimens = CashizardTheme.dimens

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        SectionHeader(title = "Profile", modifier = Modifier.padding(top = dimens.space20))
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            rows = listOf {
                PickerRow(
                    label = "Name",
                    value = state.displayName,
                    onClick = onEditProfile,
                    leading = { SettingsIconDisc(Lucide.User) },
                )
            },
        )

        SectionHeader(title = "Categories", modifier = Modifier.padding(top = dimens.space20))
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            rows = listOf(
                {
                    PickerRow(
                        label = "Edit Categories",
                        onClick = onEditCategories,
                        leading = { SettingsIconDisc(Lucide.Tag) },
                    )
                },
                {
                    PickerRow(
                        label = "Automatic Rules",
                        onClick = onEditRules,
                        leading = { SettingsIconDisc(Lucide.Sparkles) },
                    )
                },
            ),
        )

        SectionHeader(title = "Bank Connections", modifier = Modifier.padding(top = dimens.space20))
        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding),
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = buildList {
                state.bankLinks.forEach { link ->
                    add { BankLinkRowView(link, onUnlink) }
                }
                add {
                    PickerRow(
                        label = "Connect Bank Account",
                        onClick = onConnectBankAccount,
                        leading = { SettingsIconDisc(Lucide.Plus) },
                    )
                }
            },
        )

        FormCard(
            modifier = Modifier.padding(horizontal = dimens.listPadding, vertical = dimens.space20),
            cornerRadius = dimens.radiusControl,
            rows = listOf { SignOutRow(onSignOut) },
        )

        // Clears the tab bar.
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun SettingsIconDisc(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    IconDisc(
        icon = icon,
        style = IconDiscStyle.Neutral,
        size = CashizardTheme.dimens.iconDiscWallet,
        iconSize = CashizardTheme.dimens.space16,
    )
}

@Composable
private fun BankLinkRowView(link: BankLinkRow, onUnlink: (String) -> Unit) {
    CashListRow(
        title = link.accountName,
        subtitle = link.enableBankingAccountId.maskedTail(),
        minHeight = CashizardTheme.dimens.rowHeight,
        leading = { SettingsIconDisc(Lucide.Landmark) },
        trailing = {
            CircleIconButton(
                icon = Lucide.Unlink,
                onClick = { onUnlink(link.linkId) },
                contentDescription = "Unlink",
                style = CircleButtonStyle.Neutral,
                size = 30.dp,
                iconSize = 15.dp,
            )
        },
    )
}

@Composable
private fun SignOutRow(onClick: () -> Unit) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = dimens.space12),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Sign Out",
            style = CashizardTheme.typography.bodyLarge,
            color = colors.negative,
        )
    }
}

/** The last 4 characters, e.g. "••••4821" — just enough to tell accounts apart. */
private fun String.maskedTail(): String =
    if (length <= 4) this else "••••${takeLast(4)}"

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(CashizardTheme.dimens.space32),
        contentAlignment = Alignment.Center,
    ) { content() }
}
