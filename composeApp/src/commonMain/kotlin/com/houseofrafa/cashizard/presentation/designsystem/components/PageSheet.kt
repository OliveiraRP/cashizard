package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * An iOS page sheet: a card covering all but [topInset] of the screen, with
 * rounded top corners, a grabber, a scrim that fades with the sheet, a spring
 * slide-up, and swipe-down-to-dismiss.
 *
 * The sheet animates itself out before reporting [onDismissRequest], so the
 * caller can clear its navigation slot immediately when that fires.
 */
@Composable
fun PageSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    closeRequested: Boolean = false,
    topInset: Dp = 64.dp,
    content: @Composable () -> Unit,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val scope = rememberCoroutineScope()

    var sheetHeightPx by remember { mutableStateOf(0f) }
    val offsetY = remember { Animatable(0f) }
    var shown by remember { mutableStateOf(false) }
    var dismissing by remember { mutableStateOf(false) }

    // Keyed on Unit, not on the height: the sheet can be measured more than once,
    // and re-keying would cancel the entry animation mid-flight and strand the
    // sheet off-screen with only its invisible scrim still catching touches.
    LaunchedEffect(Unit) {
        snapshotFlow { sheetHeightPx }.first { it > 0f }
        offsetY.snapTo(sheetHeightPx)
        shown = true
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = 0.86f,
                stiffness = Spring.StiffnessMediumLow,
            ),
        )
    }

    fun dismiss() {
        if (dismissing) return
        dismissing = true
        scope.launch {
            offsetY.animateTo(sheetHeightPx, tween(durationMillis = 220))
            onDismissRequest()
        }
    }

    // Close requests from outside the sheet (header button, Android back) play
    // the same exit animation as a scrim tap or a swipe.
    LaunchedEffect(closeRequested) {
        if (closeRequested) dismiss()
    }

    // 1 when fully open, 0 when fully dismissed — drives the scrim.
    val progress = if (sheetHeightPx > 0f) {
        (1f - offsetY.value / sheetHeightPx).coerceIn(0f, 1f)
    } else {
        0f
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.scrim.copy(alpha = colors.scrim.alpha * progress))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = ::dismiss,
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topInset)
                .onSizeChanged { sheetHeightPx = it.height.toFloat() }
                // Hidden for the single frame between measurement and being
                // snapped off-screen, so it never flashes at its resting place.
                .graphicsLayer { alpha = if (shown) 1f else 0f }
                .offset { IntOffset(x = 0, y = offsetY.value.roundToInt()) }
                .clip(
                    RoundedCornerShape(
                        topStart = dimens.radiusSheetTop,
                        topEnd = dimens.radiusSheetTop,
                    ),
                )
                .background(colors.surfaceSheet)
                // Consume taps so they never reach the scrim underneath.
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            // Downward only; the sheet never rises above its resting point.
                            offsetY.snapTo((offsetY.value + delta).coerceAtLeast(0f))
                        }
                    },
                    onDragStopped = { velocity ->
                        val past = offsetY.value > sheetHeightPx * DISMISS_FRACTION
                        if (past || velocity > DISMISS_VELOCITY) {
                            dismiss()
                        } else {
                            offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                        }
                    },
                ),
        ) {
            Grabber()
            content()
        }
    }
}

/** The drag handle at the top of a page sheet. */
@Composable
private fun Grabber() {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = dimens.space8),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(dimens.grabberWidth)
                .height(dimens.grabberHeight)
                .clip(RoundedCornerShape(3.dp))
                .background(colors.grabber),
        )
    }
}

/** Drag past this share of the sheet height and it dismisses. */
private const val DISMISS_FRACTION = 0.25f

/** …or fling downward faster than this (px/s) regardless of distance. */
private const val DISMISS_VELOCITY = 1200f
