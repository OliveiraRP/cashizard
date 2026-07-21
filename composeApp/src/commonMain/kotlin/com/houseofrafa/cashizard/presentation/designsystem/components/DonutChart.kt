package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/** One arc of a [DonutChart]. [fraction] is its share of the whole, 0f..1f. */
data class DonutSlice(
    val id: String,
    val color: Color,
    val fraction: Float,
    /** Dimmed to 0.35 when another slice is the focus. */
    val dimmed: Boolean = false,
)

/**
 * A ring chart drawn on Canvas — no chart library. Arcs run clockwise from
 * twelve o'clock, and a tap is matched to a slice by its angle from the centre,
 * ignoring taps that land in the hole or outside the ring.
 */
@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    diameter: Dp = 232.dp,
    strokeWidth: Dp = 28.dp,
    onSliceClick: ((String) -> Unit)? = null,
    center: @Composable () -> Unit = {},
) {
    val emptyColor = CashizardTheme.colors.fillTrack

    Box(
        modifier = modifier.size(diameter),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(diameter)
                .then(
                    if (onSliceClick != null) {
                        Modifier.pointerInput(slices) {
                            detectTapGestures { offset ->
                                sliceAt(offset, size.width.toFloat(), strokeWidth.toPx(), slices)
                                    ?.let(onSliceClick)
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
        ) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(inset, inset)

            if (slices.isEmpty()) {
                drawArc(
                    color = emptyColor,
                    startAngle = START_ANGLE,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke),
                )
                return@Canvas
            }

            var startAngle = START_ANGLE
            slices.forEach { slice ->
                val sweep = slice.fraction * 360f
                drawArc(
                    color = if (slice.dimmed) slice.color.copy(alpha = 0.35f) else slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke),
                )
                startAngle += sweep
            }
        }

        center()
    }
}

/** Twelve o'clock: Compose measures arc angles clockwise from three o'clock. */
private const val START_ANGLE = -90f

/**
 * The slice under [offset], or null when the tap missed the ring. Angles are
 * normalised to the same clockwise-from-twelve sweep the arcs are drawn with.
 */
private fun sliceAt(
    offset: Offset,
    canvasSize: Float,
    strokeWidth: Float,
    slices: List<DonutSlice>,
): String? {
    val centre = canvasSize / 2f
    val dx = offset.x - centre
    val dy = offset.y - centre
    val distance = sqrt(dx * dx + dy * dy)

    val outer = centre
    val inner = centre - strokeWidth
    if (distance < inner || distance > outer) return null

    // atan2 gives -180..180 from three o'clock; shift so 0 is twelve o'clock.
    val degrees = (toDegrees(atan2(dy, dx)) - START_ANGLE + 360f) % 360f

    var sweptSoFar = 0f
    slices.forEach { slice ->
        val sweep = slice.fraction * 360f
        if (degrees >= sweptSoFar && degrees < sweptSoFar + sweep) return slice.id
        sweptSoFar += sweep
    }
    return slices.lastOrNull()?.id
}

/** kotlin.math has no toDegrees in common code. */
private fun toDegrees(radians: Float): Float = radians * 180f / PI.toFloat()
