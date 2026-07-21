package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * An uppercase section label with an optional trailing action ("See all ›").
 * The label sits 22dp from the edge so it aligns with the text inside the cards
 * below, which are inset 16dp and pad 16dp internally.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    style: TextStyle = CashizardTheme.typography.sectionLabel,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 22.dp,
                end = dimens.screenPadding,
                top = dimens.space16,
                bottom = 7.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            style = style,
            color = colors.textTertiary,
            modifier = Modifier.weight(1f),
        )
        if (actionLabel != null && onActionClick != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onActionClick,
                ),
            ) {
                Text(
                    text = actionLabel,
                    style = CashizardTheme.typography.sectionAction,
                    color = colors.accent,
                )
                Icon(
                    imageVector = Lucide.ChevronRight,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.padding(start = 1.dp).size(15.dp),
                )
            }
        }
    }
}
