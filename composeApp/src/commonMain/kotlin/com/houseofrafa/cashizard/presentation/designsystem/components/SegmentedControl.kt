package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme

/**
 * iOS-style segmented control. The selected segment gets a raised thumb; the
 * rest are transparent over the track. Segments share equal width.
 */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val trackShape = RoundedCornerShape(dimens.radiusSegmentTrack)
    val thumbShape = RoundedCornerShape(dimens.radiusSegmentThumb)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(trackShape)
            .background(colors.fillSegmentTrack)
            .padding(2.dp),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val thumbColor by animateColorAsState(
                if (selected) colors.segmentThumb else colors.segmentThumb.copy(alpha = 0f),
            )
            val textColor = if (selected) colors.textPrimary else colors.textSecondary
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 1.dp)
                    .clip(thumbShape)
                    .background(thumbColor, thumbShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(index) },
                    )
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = CashizardTheme.typography.footnoteStrong,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}
