package com.houseofrafa.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// =============================================================================
// SHAPE TOKENS
// Continuous rounded corners — matches iOS's squircle / superellipse aesthetic.
// Accessed via CashizardTheme.shapes
// =============================================================================

@Immutable
data class CashizardShapes(
    /** 4 dp — tags, small badges. */
    val extraSmall: Shape  = RoundedCornerShape(4.dp),
    /** 8 dp — input fields, small chips. */
    val small: Shape       = RoundedCornerShape(8.dp),
    /** 12 dp — buttons, medium chips, compact cards. */
    val medium: Shape      = RoundedCornerShape(12.dp),
    /** 16 dp — standard cards, list sections, bottom sheet top corners. */
    val large: Shape       = RoundedCornerShape(16.dp),
    /** 20 dp — large cards, prominent tiles. */
    val extraLarge: Shape  = RoundedCornerShape(20.dp),
    /** 28 dp — hero cards, feature banners. */
    val huge: Shape        = RoundedCornerShape(28.dp),
    /** 50% — pill buttons, avatars, FABs. */
    val full: Shape        = RoundedCornerShape(percent = 50),
)

internal val LocalCashizardShapes = staticCompositionLocalOf { CashizardShapes() }