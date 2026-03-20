package com.houseofrafa.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// =============================================================================
// TYPOGRAPHY TOKENS
// Scale inspired by iOS type system (SF Pro sizes) mapped to Material3 roles.
// Accessed via CashizardTheme.typography
// =============================================================================

@Immutable
data class CashizardTypography(

    // -------------------------------------------------------------------------
    // Display  (hero numbers, large splashes — used sparingly)
    // -------------------------------------------------------------------------
    /** Large balance or hero number. */
    val displayLarge: TextStyle,
    /** Section hero number or onboarding title. */
    val displaySmall: TextStyle,

    // -------------------------------------------------------------------------
    // Headline  (screen titles, modal headers)
    // -------------------------------------------------------------------------
    /** Navigation bar large title — iOS Large Title style. */
    val headlineLarge: TextStyle,
    /** Standard navigation bar title. */
    val headlineMedium: TextStyle,
    /** Section header inside a list. */
    val headlineSmall: TextStyle,

    // -------------------------------------------------------------------------
    // Title  (card titles, dialog titles)
    // -------------------------------------------------------------------------
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,

    // -------------------------------------------------------------------------
    // Body  (main readable content)
    // -------------------------------------------------------------------------
    /** Primary body text. */
    val bodyLarge: TextStyle,
    /** Secondary body / list row primary label. */
    val bodyMedium: TextStyle,
    /** Compact body / list row secondary label. */
    val bodySmall: TextStyle,

    // -------------------------------------------------------------------------
    // Label  (buttons, tags, captions)
    // -------------------------------------------------------------------------
    /** Button text, tab bar labels. */
    val labelLarge: TextStyle,
    /** Chip text, secondary labels. */
    val labelMedium: TextStyle,
    /** Caption text, footnotes, timestamps. */
    val labelSmall: TextStyle,
)

// =============================================================================
// Default typography  (system font — inherits SF Pro on iOS, Roboto on Android)
// =============================================================================
internal val defaultTypography = CashizardTypography(

    // Display
    displayLarge = TextStyle(
        fontSize   = 48.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp,
    ),
    displaySmall = TextStyle(
        fontSize   = 34.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 41.sp,
        letterSpacing = 0.sp,
    ),

    // Headline
    headlineLarge = TextStyle(
        fontSize   = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 34.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontSize   = 22.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontSize   = 17.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp,
    ),

    // Title
    titleLarge = TextStyle(
        fontSize   = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 25.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontSize   = 17.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleSmall = TextStyle(
        fontSize   = 15.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = (-0.2).sp,
    ),

    // Body
    bodyLarge = TextStyle(
        fontSize   = 17.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp,
    ),
    bodyMedium = TextStyle(
        fontSize   = 15.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = (-0.2).sp,
    ),
    bodySmall = TextStyle(
        fontSize   = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp,
        letterSpacing = (-0.1).sp,
    ),

    // Label
    labelLarge = TextStyle(
        fontSize   = 17.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.sp,
        letterSpacing = (-0.2).sp,
    ),
    labelMedium = TextStyle(
        fontSize   = 13.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 18.sp,
        letterSpacing = (-0.1).sp,
    ),
    labelSmall = TextStyle(
        fontSize   = 11.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 13.sp,
        letterSpacing = 0.sp,
    ),
)

internal val LocalCashizardTypography = staticCompositionLocalOf { defaultTypography }