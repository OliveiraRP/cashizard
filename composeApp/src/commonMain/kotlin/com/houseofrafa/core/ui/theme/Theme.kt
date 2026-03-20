package com.houseofrafa.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// =============================================================================
// MATERIAL3 COLOR SCHEMES
// These keep Material3 components (Button, Scaffold, etc.) consistent with
// the Cashizard palette. CashizardColors is the source of truth — M3 roles
// are mapped from it so they never diverge.
// =============================================================================

private val material3DarkColorScheme = darkColorScheme(
    primary              = Purple60,
    onPrimary            = White,
    primaryContainer     = Purple30,
    onPrimaryContainer   = Purple90,
    secondary            = Indigo60,
    onSecondary          = White,
    secondaryContainer   = Indigo30,
    onSecondaryContainer = Indigo90,
    tertiary             = Green,
    onTertiary           = Neutral100,
    tertiaryContainer    = Color(0xFF0D3320),
    onTertiaryContainer  = Color(0xFFA0F4B4),
    background           = Neutral100,
    onBackground         = White,
    surface              = Neutral98,
    onSurface            = White,
    surfaceVariant       = Neutral97,
    onSurfaceVariant     = Neutral60,
    error                = Red,
    onError              = White,
    errorContainer       = Color(0xFF3D1210),
    onErrorContainer     = Color(0xFFFFB4AE),
    outline              = Neutral90,
    outlineVariant       = Neutral95,
    scrim                = Neutral100,
    inverseSurface       = Neutral10,
    inverseOnSurface     = Neutral98,
    inversePrimary       = Purple40,
)

private val material3LightColorScheme = lightColorScheme(
    primary              = Purple40,
    onPrimary            = White,
    primaryContainer     = Purple95,
    onPrimaryContainer   = Purple20,
    secondary            = Indigo40,
    onSecondary          = White,
    secondaryContainer   = Indigo90,
    onSecondaryContainer = Indigo20,
    tertiary             = GreenLight,
    onTertiary           = White,
    tertiaryContainer    = Color(0xFFD4EDDA),
    onTertiaryContainer  = Color(0xFF003912),
    background           = Neutral10,
    onBackground         = Neutral100,
    surface              = White,
    onSurface            = Neutral100,
    surfaceVariant       = Neutral20,
    onSurfaceVariant     = Neutral80,
    error                = RedLight,
    onError              = White,
    errorContainer       = Color(0xFFFFE5E3),
    onErrorContainer     = Color(0xFF5C1715),
    outline              = Neutral40,
    outlineVariant       = Neutral20,
    scrim                = Neutral100,
    inverseSurface       = Neutral98,
    inverseOnSurface     = Neutral10,
    inversePrimary       = Purple60,
)

// =============================================================================
// THEME COMPOSABLE
// Wrap your entire app (or individual screens) with CashizardTheme.
// Access tokens via:
//   CashizardTheme.colors.textPrimary
//   CashizardTheme.typography.headlineLarge
//   CashizardTheme.spacing.md
//   CashizardTheme.shapes.large
// =============================================================================

@Composable
fun CashizardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors     = if (darkTheme) CashizardDarkColors  else CashizardLightColors
    val colorScheme = if (darkTheme) material3DarkColorScheme else material3LightColorScheme

    CompositionLocalProvider(
        LocalCashizardColors     provides colors,
        LocalCashizardTypography provides defaultTypography,
        LocalCashizardSpacing    provides CashizardSpacing(),
        LocalCashizardShapes     provides CashizardShapes(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content     = content,
        )
    }
}

// =============================================================================
// THEME ACCESSORS
// Use these in composables — e.g. CashizardTheme.colors.textPrimary
// =============================================================================

object CashizardTheme {
    val colors: CashizardColors
        @Composable @ReadOnlyComposable
        get() = LocalCashizardColors.current

    val typography: CashizardTypography
        @Composable @ReadOnlyComposable
        get() = LocalCashizardTypography.current

    val spacing: CashizardSpacing
        @Composable @ReadOnlyComposable
        get() = LocalCashizardSpacing.current

    val shapes: CashizardShapes
        @Composable @ReadOnlyComposable
        get() = LocalCashizardShapes.current
}
