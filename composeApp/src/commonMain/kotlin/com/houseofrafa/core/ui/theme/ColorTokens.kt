package com.houseofrafa.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// =============================================================================
// SEMANTIC TOKENS
// These are the colors your composables should reference.
// Never hard-code a primitive (Purple60, etc.) in a composable — use these.
// =============================================================================

@Immutable
data class CashizardColors(

    // -------------------------------------------------------------------------
    // Text
    // -------------------------------------------------------------------------
    /** Primary body text, headings — highest emphasis. */
    val textPrimary: Color,
    /** Secondary labels, subtitles — medium emphasis. */
    val textSecondary: Color,
    /** Tertiary captions, placeholders — low emphasis. */
    val textTertiary: Color,
    /** Disabled / non-interactive text. */
    val textDisabled: Color,
    /** Text placed on a brand-colored (filled) surface. */
    val textOnBrand: Color,
    /** Text placed on a dark surface when in light mode (inverse). */
    val textInverse: Color,

    // -------------------------------------------------------------------------
    // Background  (screen-level)
    // -------------------------------------------------------------------------
    /** Main screen background — black in dark mode (iOS native). */
    val backgroundPrimary: Color,
    /** Secondary background — used for grouped list insets, modal sheets. */
    val backgroundSecondary: Color,
    /** Tertiary background — nested sections within secondary surfaces. */
    val backgroundTertiary: Color,

    // -------------------------------------------------------------------------
    // Surface  (component-level: cards, bottom sheets, dialogs)
    // -------------------------------------------------------------------------
    /** Default card / bottom-sheet surface. */
    val surfacePrimary: Color,
    /** Elevated card surface (adds slight separation from background). */
    val surfaceElevated: Color,
    /** Overlay surface — e.g. contextual menus, popovers. */
    val surfaceOverlay: Color,

    // -------------------------------------------------------------------------
    // Brand
    // -------------------------------------------------------------------------
    /** Primary brand color — used for CTAs, active tab indicators, focus rings. */
    val brandPrimary: Color,
    /** Subdued primary brand — used for filled chips, secondary buttons. */
    val brandPrimarySubtle: Color,
    /** Secondary brand color. */
    val brandSecondary: Color,
    /** Subdued secondary brand — used for secondary chips. */
    val brandSecondarySubtle: Color,

    // -------------------------------------------------------------------------
    // Border / Divider
    // -------------------------------------------------------------------------
    /** Default separator between list rows or section dividers. */
    val borderPrimary: Color,
    /** Subtle border — input field outlines, card strokes. */
    val borderSecondary: Color,
    /** Focused / active input border. */
    val borderFocused: Color,

    // -------------------------------------------------------------------------
    // Feedback
    // -------------------------------------------------------------------------
    /** Success state — confirmations, completed actions. */
    val feedbackSuccess: Color,
    /** Subdued success container background. */
    val feedbackSuccessSubtle: Color,
    /** Error / destructive state. */
    val feedbackError: Color,
    /** Subdued error container background. */
    val feedbackErrorSubtle: Color,
    /** Warning state — budget nearing limit, etc. */
    val feedbackWarning: Color,
    /** Subdued warning container background. */
    val feedbackWarningSubtle: Color,
    /** Informational state. */
    val feedbackInfo: Color,
    /** Subdued info container background. */
    val feedbackInfoSubtle: Color,

    // -------------------------------------------------------------------------
    // Budget-app domain colors
    // -------------------------------------------------------------------------
    /** Positive transaction / income amount. */
    val income: Color,
    /** Subdued income background — e.g. pill behind income value. */
    val incomeSubtle: Color,
    /** Negative transaction / expense amount. */
    val expense: Color,
    /** Subdued expense background. */
    val expenseSubtle: Color,
    /** Savings / goal progress. */
    val savings: Color,
    /** Subdued savings background. */
    val savingsSubtle: Color,
)

// =============================================================================
// Dark scheme  (primary — black iOS background)
// =============================================================================
internal val CashizardDarkColors = CashizardColors(
    // Text
    textPrimary           = White,
    textSecondary         = Neutral60,
    textTertiary          = Neutral80,
    textDisabled          = Neutral90,
    textOnBrand           = White,
    textInverse           = Neutral100,

    // Background
    backgroundPrimary     = Neutral100, // #000000
    backgroundSecondary   = Neutral98,  // #1C1C1E
    backgroundTertiary    = Neutral97,  // #2C2C2E

    // Surface
    surfacePrimary        = Neutral98,  // #1C1C1E
    surfaceElevated       = Neutral97,  // #2C2C2E
    surfaceOverlay        = Neutral95,  // #3A3A3C

    // Brand
    brandPrimary          = Purple60,   // #BF5AF2
    brandPrimarySubtle    = Purple30,   // #3D1461
    brandSecondary        = Indigo60,   // #7977F0
    brandSecondarySubtle  = Indigo30,   // #2A2980

    // Border
    borderPrimary         = Neutral90,  // #48484A
    borderSecondary       = Neutral95,  // #3A3A3C
    borderFocused         = Purple60,   // #BF5AF2

    // Feedback
    feedbackSuccess       = Green,
    feedbackSuccessSubtle = Color(0xFF0D3320),
    feedbackError         = Red,
    feedbackErrorSubtle   = Color(0xFF3D1210),
    feedbackWarning       = Orange,
    feedbackWarningSubtle = Color(0xFF3D2500),
    feedbackInfo          = Blue,
    feedbackInfoSubtle    = Color(0xFF002050),

    // Budget domain
    income                = Green,
    incomeSubtle          = Color(0xFF0D3320),
    expense               = Red,
    expenseSubtle         = Color(0xFF3D1210),
    savings               = Blue,
    savingsSubtle         = Color(0xFF002050),
)

// =============================================================================
// Light scheme  (fallback for system light mode)
// =============================================================================
internal val CashizardLightColors = CashizardColors(
    // Text
    textPrimary           = Neutral100,
    textSecondary         = Neutral80,
    textTertiary          = Neutral60,
    textDisabled          = Neutral40,
    textOnBrand           = White,
    textInverse           = White,

    // Background
    backgroundPrimary     = Neutral10,  // #F2F2F7
    backgroundSecondary   = White,
    backgroundTertiary    = Neutral20,  // #E5E5EA

    // Surface
    surfacePrimary        = White,
    surfaceElevated       = Neutral05,
    surfaceOverlay        = Neutral20,

    // Brand
    brandPrimary          = Purple40,   // #5C2587
    brandPrimarySubtle    = Purple95,   // #F6EAFF
    brandSecondary        = Indigo40,   // #4240BF
    brandSecondarySubtle  = Indigo90,   // #E0E0FF

    // Border
    borderPrimary         = Neutral40,
    borderSecondary       = Neutral20,
    borderFocused         = Purple40,

    // Feedback
    feedbackSuccess       = GreenLight,
    feedbackSuccessSubtle = Color(0xFFD4EDDA),
    feedbackError         = RedLight,
    feedbackErrorSubtle   = Color(0xFFFFE5E3),
    feedbackWarning       = OrangeLight,
    feedbackWarningSubtle = Color(0xFFFFF3CD),
    feedbackInfo          = BlueLight,
    feedbackInfoSubtle    = Color(0xFFD0E8FF),

    // Budget domain
    income                = GreenLight,
    incomeSubtle          = Color(0xFFD4EDDA),
    expense               = RedLight,
    expenseSubtle         = Color(0xFFFFE5E3),
    savings               = BlueLight,
    savingsSubtle         = Color(0xFFD0E8FF),
)

// =============================================================================
// CompositionLocal — accessed via CashizardTheme.colors
// =============================================================================
internal val LocalCashizardColors = staticCompositionLocalOf { CashizardDarkColors }