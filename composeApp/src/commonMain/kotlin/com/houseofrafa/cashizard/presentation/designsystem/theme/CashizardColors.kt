package com.houseofrafa.cashizard.presentation.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Dark-only color tokens for Cashizard. Extracted from `Budget Manager.dc.html`
 * — an iOS-flavoured near-black palette with a single iOS-blue accent. Screens
 * must read colors from [CashizardTheme.colors], never hardcode.
 */
@Immutable
data class CashizardColors(
    // Backgrounds & surfaces. Each maps to a distinct role in the design.
    val background: Color,       // app background
    val surface: Color,          // grouped form/list card (radius 16)
    val surfaceVariant: Color,   // inset rows & pickers (radius 14)
    val surfaceHigh: Color,      // icon discs / raised element
    val surfaceRaised: Color,    // transfer discs, popovers
    val surfaceChip: Color,      // pills, chips, small circular buttons
    val surfaceSheet: Color,     // iOS page sheets
    val accountHeader: Color,    // tint over an account card's header row
    val popover: Color,          // context menu surface
    val popoverBorder: Color,
    val popoverSeparator: Color,
    val tabBar: Color,           // translucent bottom bar
    val scrim: Color,            // modal scrim behind sheets

    // Lines
    val separator: Color,        // hairline separators

    // Translucent control fills (iOS system fills over any surface)
    val fillTrack: Color,        // progress bar tracks
    val fillControl: Color,      // neutral circular buttons
    val fillSearch: Color,       // search field
    val fillSegmentTrack: Color, // segmented control track
    val fillKey: Color,          // keypad keys
    val grabber: Color,          // sheet drag handle
    val segmentThumb: Color,     // selected segment

    // Accent
    val accent: Color,           // primary iOS blue
    val accentSubtle: Color,     // accent-tinted pill backgrounds
    val accentPressed: Color,    // lighter blue for pressed/hover
    val onAccent: Color,         // content on accent

    // Text (iOS label hierarchy over the #EBEBF5 base)
    val textPrimary: Color,
    val textSecondary: Color,    // chevrons, prominent secondary
    val textTertiary: Color,     // captions, footers
    val textQuaternary: Color,   // field leading icons
    val textPlaceholder: Color,  // input placeholders, reveal toggles

    // Semantic amounts
    val positive: Color,         // income / gains (green)
    val negative: Color,         // expense / losses (red)
    val errorText: Color,        // inline form error copy (softer than negative)
)

/** The single dark palette. Cashizard is dark-only by design. */
val CashizardDarkColors: CashizardColors = CashizardColors(
    background = Color(0xFF0D0D0F),
    surface = Color(0xFF1A1A1E),
    surfaceVariant = Color(0xFF1F1F22),
    surfaceHigh = Color(0xFF2C2C2E),
    surfaceRaised = Color(0xFF3A3A3C),
    surfaceChip = Color(0xFF1C1C1E),
    surfaceSheet = Color(0xFF161618),
    accountHeader = Color(0x08FFFFFF), // white @ 3%
    popover = Color(0xFA2C2C2E),
    popoverBorder = Color(0x14FFFFFF),
    popoverSeparator = Color(0x1AFFFFFF), // white @ 10%
    tabBar = Color(0xF0121215),
    scrim = Color(0x99000000),

    separator = Color(0x12FFFFFF), // white @ 7%

    fillTrack = Color(0x14FFFFFF),           // white @ 8%
    fillControl = Color(0x3D767680),         // iOS tertiary fill @ 24%
    fillSearch = Color(0x33767680),          // @ 20%
    fillSegmentTrack = Color(0x2E767680),    // @ 18%
    fillKey = Color(0x0FFFFFFF),             // white @ 6%
    grabber = Color(0x2EFFFFFF),             // white @ 18%
    segmentThumb = Color(0xFF636366),

    accent = Color(0xFF0A84FF),
    accentSubtle = Color(0x260A84FF), // accent @ 15%
    accentPressed = Color(0xFF409CFF),
    onAccent = Color(0xFFFFFFFF),

    // #EBEBF5 at the alphas the design actually uses
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xBFEBEBF5),   // 75%
    textTertiary = Color(0x80EBEBF5),    // 50%
    textQuaternary = Color(0x66EBEBF5),  // 40%
    textPlaceholder = Color(0x59EBEBF5), // 35%

    positive = Color(0xFF32D74A),
    negative = Color(0xFFFF453A),
    errorText = Color(0xFFFF6961),
)
