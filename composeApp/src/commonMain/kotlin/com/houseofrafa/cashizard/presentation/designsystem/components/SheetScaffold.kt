package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * Header + body for content inside a [PageSheet]. The header is a balanced
 * three-slot row — leading control, centered title, trailing control — where an
 * absent control still reserves its width so the title stays centered.
 */
@Composable
fun SheetScaffold(
    title: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = dimens.rowHeightMin)
                .padding(horizontal = dimens.space16, vertical = dimens.space16),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.align(Alignment.CenterStart)) {
                if (leading != null) leading() else SlotSpacer()
            }
            Text(
                text = title,
                style = CashizardTheme.typography.headline,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = dimens.iconDisc + dimens.space8),
            )
            Box(Modifier.align(Alignment.CenterEnd)) {
                if (trailing != null) trailing() else SlotSpacer()
            }
        }
        content()
    }
}

/** Reserves a control's footprint so the title stays optically centered. */
@Composable
private fun SlotSpacer() {
    Box(Modifier.size(CashizardTheme.dimens.iconDisc))
}

/** Closes the sheet from its root entry. */
@Composable
fun SheetCloseButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleIconButton(
        icon = Lucide.X,
        onClick = onClick,
        contentDescription = "Close",
        style = CircleButtonStyle.Neutral,
        modifier = modifier,
    )
}

/** Pops one entry of the sheet's inner stack. */
@Composable
fun SheetBackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleIconButton(
        icon = Lucide.ChevronLeft,
        onClick = onClick,
        contentDescription = "Back",
        style = CircleButtonStyle.Neutral,
        iconSize = 22.dp,
        modifier = modifier,
    )
}
