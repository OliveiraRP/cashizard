package com.houseofrafa.core.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.houseofrafa.core.ui.theme.CashizardTheme

@Composable
fun IconBox(
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    shape: Shape = CashizardTheme.shapes.medium,
    contentDescription: String? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor),
    ) {
        Icon(
            imageVector    = icon,
            contentDescription = contentDescription,
            tint           = tint,
            modifier       = Modifier.size(iconSize),
        )
    }
}
