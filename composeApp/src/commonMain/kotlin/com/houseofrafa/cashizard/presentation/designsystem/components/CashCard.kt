package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * The standard elevated surface container. A rounded card on the app
 * background. Content is laid out in a [Column].
 */
@Composable
fun CashCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CashizardTheme.dimens.radiusCard,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val base = modifier
        .clip(shape)
        .background(CashizardTheme.colors.surface, shape)
    val withClick = if (onClick != null) base.clickable(onClick = onClick) else base
    Column(modifier = withClick, content = content)
}
