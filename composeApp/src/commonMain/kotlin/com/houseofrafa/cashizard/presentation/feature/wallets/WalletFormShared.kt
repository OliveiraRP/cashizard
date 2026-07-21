package com.houseofrafa.cashizard.presentation.feature.wallets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Banknote
import com.composables.icons.lucide.CalendarDays
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Flag
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.PiggyBank
import com.composables.icons.lucide.TrendingUp
import com.houseofrafa.cashizard.domain.model.WalletType
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.components.FormCard
import com.houseofrafa.cashizard.presentation.designsystem.components.InsetRow

/**
 * Each wallet type's glyph, as the design assigns them. Taken straight from
 * Lucide rather than through the icon registry, which only carries the names
 * the database uses for categories and wallets.
 */
fun WalletType.typeIcon(): ImageVector = when (this) {
    WalletType.EXPENSE -> Lucide.Banknote
    WalletType.GOAL -> Lucide.Flag
    WalletType.BUDGET -> Lucide.CalendarDays
    WalletType.SAVINGS -> Lucide.PiggyBank
    WalletType.INVESTMENT -> Lucide.TrendingUp
}

/** A filled check when selected, a hollow ring when not. Shared by wallet forms. */
@Composable
fun WalletSelectionMark(selected: Boolean) {
    val colors = CashizardTheme.colors
    if (selected) {
        Icon(
            imageVector = Lucide.CircleCheck,
            contentDescription = "Selected",
            tint = colors.accent,
            modifier = Modifier.size(20.dp),
        )
    } else {
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(1.5.dp, colors.textPlaceholder, CircleShape),
        )
    }
}

/** The wallet-type list, pushed from both the add and edit forms' Type row. */
@Composable
fun WalletTypePicker(
    selected: WalletType,
    onSelect: (WalletType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = CashizardTheme.dimens

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimens.listPadding),
    ) {
        FormCard(
            cornerRadius = dimens.radiusControl,
            separatorInset = dimens.space16 + dimens.iconDiscWallet + dimens.space12,
            rows = walletTypeOptions.map { (type, description) ->
                {
                    InsetRow(
                        label = type.label,
                        subtitle = description,
                        leadingIcon = type.typeIcon(),
                        minHeight = 60.dp,
                        onClick = { onSelect(type) },
                        trailing = { WalletSelectionMark(type == selected) },
                    )
                }
            },
        )
    }
}
