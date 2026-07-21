package com.houseofrafa.cashizard.presentation.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale, corner radii and standard sizes. Read from
 * [CashizardTheme.dimens]; screens never hardcode raw dp for these.
 */
@Immutable
data class Dimens(
    // Spacing scale (4 / 8 / 12 / 16 / 20 / 24 …)
    val space2: Dp = 2.dp,
    val space4: Dp = 4.dp,
    val space8: Dp = 8.dp,
    val space12: Dp = 12.dp,
    val space16: Dp = 16.dp,
    val space20: Dp = 20.dp,
    val space24: Dp = 24.dp,
    val space32: Dp = 32.dp,

    /** Headers, titles and the auth forms sit at 20dp. */
    val screenPadding: Dp = 20.dp,
    /** Grouped list cards are inset slightly further, at 16dp. */
    val listPadding: Dp = 16.dp,

    // Corner radii
    val radiusSmall: Dp = 10.dp,
    val radiusChip: Dp = 12.dp,
    val radiusKey: Dp = 10.dp,       // keypad keys
    val radiusControl: Dp = 14.dp,   // buttons, inset rows
    val radiusCard: Dp = 16.dp,      // transaction & form cards
    val radiusCardMedium: Dp = 18.dp, // analytics card
    val radiusCardLarge: Dp = 20.dp,  // wallet & account cards
    val radiusSheetTop: Dp = 28.dp,   // page sheet top corners
    val radiusSegmentTrack: Dp = 9.dp,
    val radiusSegmentThumb: Dp = 7.dp,
    val radiusPill: Dp = 999.dp,

    // Sizes
    val hairline: Dp = 1.dp,
    val rowHeightMin: Dp = 44.dp,
    val rowHeight: Dp = 52.dp,
    val rowHeightCompact: Dp = 48.dp,

    // Icon disc sizes, each tied to a row style in the design
    val iconDiscWallet: Dp = 30.dp,   // wallet rows (neutral disc)
    val iconDiscAnalytics: Dp = 32.dp, // analytics rows
    val iconDisc: Dp = 36.dp,          // transaction rows, circular buttons
    val iconDiscChip: Dp = 44.dp,      // category chips
    val iconDiscEmpty: Dp = 68.dp,     // empty-state glyph
    val fabSize: Dp = 56.dp,

    val buttonHeight: Dp = 50.dp,
    val keyHeight: Dp = 40.dp,
    /**
     * Left inset for hairlines inside a grouped card. The design aligns them to
     * the text, i.e. 16 padding + leading width + 12 gap — 46 for a bare 18dp
     * glyph, 58 for a 30dp disc, 64 for a 36dp disc.
     */
    val formSeparatorInset: Dp = 46.dp,
    val grabberWidth: Dp = 36.dp,
    val grabberHeight: Dp = 5.dp,
    val progressBarHeight: Dp = 3.dp,
    val progressBarRadius: Dp = 2.dp,
)
