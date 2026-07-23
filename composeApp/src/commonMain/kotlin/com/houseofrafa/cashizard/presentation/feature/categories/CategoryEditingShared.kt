package com.houseofrafa.cashizard.presentation.feature.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.domain.model.CategoryGroup
import com.houseofrafa.cashizard.domain.model.TransactionType
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import com.houseofrafa.cashizard.presentation.designsystem.tokens.CategoryColors

/** A group as a color dot, its name, and an optional trailing control. */
@Composable
internal fun GroupRow(
    group: CategoryGroup,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    value: String? = null,
    emphasized: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimens.rowHeightCompact)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = dimens.space16, vertical = dimens.space8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.space12),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(CategoryColors.parse(group.color), CircleShape),
        )
        Text(
            text = group.name,
            style = CashizardTheme.typography.bodyLarge.copy(
                fontWeight = if (emphasized) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        if (value != null) {
            Text(
                text = value,
                style = CashizardTheme.typography.footnote,
                color = colors.textTertiary,
            )
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Lucide.ChevronRight,
                contentDescription = null,
                tint = colors.textPlaceholder,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
internal fun SelectionMark(selected: Boolean) {
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

@Composable
internal fun FormError(message: String?) {
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

@Composable
internal fun Hint(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(CashizardTheme.dimens.space32),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = CashizardTheme.typography.footnote,
            color = CashizardTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * The group types the segmented control offers when creating a group. Transfer is
 * excluded: transfers use a single fixed system group, so users never author
 * transfer categories or groups.
 */
internal val groupTypeOptions: List<Pair<TransactionType, String>> = listOf(
    TransactionType.EXPENSE to "Expense",
    TransactionType.INCOME to "Income",
)

/** The type's name, for prose in hints. */
internal val TransactionType.label: String
    get() = when (this) {
        TransactionType.EXPENSE -> "Expense"
        TransactionType.INCOME -> "Income"
        TransactionType.TRANSFER -> "Transfer"
    }
