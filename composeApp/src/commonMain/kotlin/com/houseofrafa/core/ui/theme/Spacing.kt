package com.houseofrafa.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// =============================================================================
// SPACING TOKENS
// 4 dp base unit grid — consistent with iOS HIG and Material3.
// Accessed via CashizardTheme.spacing
// =============================================================================

@Immutable
data class CashizardSpacing(
    /** 2 dp — hairline gaps, icon badge offsets. */
    val xxxs: Dp = 2.dp,
    /** 4 dp — tight internal padding (icon-to-label). */
    val xxs: Dp  = 4.dp,
    /** 8 dp — compact component padding, small gaps between elements. */
    val xs: Dp   = 8.dp,
    /** 12 dp — inner padding for chips, compact list rows. */
    val sm: Dp   = 12.dp,
    /** 16 dp — standard content padding, card inner padding. */
    val md: Dp   = 16.dp,
    /** 20 dp — comfortable content padding on larger cards. */
    val lg: Dp   = 20.dp,
    /** 24 dp — section vertical spacing, modal header padding. */
    val xl: Dp   = 24.dp,
    /** 32 dp — spacing between major screen sections. */
    val xxl: Dp  = 32.dp,
    /** 48 dp — screen top/bottom padding, large visual separations. */
    val xxxl: Dp = 48.dp,

    // -------------------------------------------------------------------------
    // Semantic aliases — prefer these over raw values in composables
    // -------------------------------------------------------------------------
    /** Horizontal padding applied to screen-level content. */
    val screenHorizontal: Dp = 16.dp,
    /** Vertical padding at the top of a screen (below nav bar). */
    val screenTop: Dp        = 16.dp,
    /** Vertical padding at the bottom of a screen (above tab bar). */
    val screenBottom: Dp     = 24.dp,
    /** Margin top applied to main headers */
    val marginTopLarge: Dp   = 84.dp,
    /** Standard inner padding for cards and list rows. */
    val cardPadding: Dp      = 16.dp,
    /** Vertical gap between list items. */
    val listItemGap: Dp      = 8.dp,
    /** Spacing between icon and its adjacent label. */
    val iconLabelGap: Dp     = 8.dp,
    /** Standard height for an interactive list row. */
    val listRowHeight: Dp    = 56.dp,
    /** Standard icon size. */
    val iconSize: Dp         = 24.dp,
    /** Small icon size (e.g. trailing chevrons, indicators). */
    val iconSizeSmall: Dp    = 16.dp,
    /** Large icon / illustration size. */
    val iconSizeLarge: Dp    = 32.dp,
)

internal val LocalCashizardSpacing = staticCompositionLocalOf { CashizardSpacing() }