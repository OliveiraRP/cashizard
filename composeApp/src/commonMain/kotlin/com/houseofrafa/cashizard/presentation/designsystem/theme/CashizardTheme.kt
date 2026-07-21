package com.houseofrafa.cashizard.presentation.designsystem.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalCashizardColors = staticCompositionLocalOf<CashizardColors> {
    error("CashizardColors not provided — wrap content in CashizardTheme { }")
}
private val LocalDimens = staticCompositionLocalOf { Dimens() }
private val LocalCashizardTypography = staticCompositionLocalOf { CashizardTypography() }

/**
 * Single entry point to the design system. Provides colors, dimensions and
 * typography via composition locals. Cashizard is dark-only, so there is no
 * light variant. This deliberately does NOT use MaterialTheme — we set the
 * Material content color / text style defaults so the few Material primitives
 * we reuse (Text, Icon) pick up our tokens instead of Material defaults.
 */
@Composable
fun CashizardTheme(content: @Composable () -> Unit) {
    val colors = CashizardDarkColors
    val typography = CashizardTypography()
    CompositionLocalProvider(
        LocalCashizardColors provides colors,
        LocalDimens provides Dimens(),
        LocalCashizardTypography provides typography,
        LocalContentColor provides colors.textPrimary,
        LocalTextStyle provides typography.body,
        LocalIndication provides ripple(),
        content = content,
    )
}

/** Accessors for design-system tokens. */
object CashizardTheme {
    val colors: CashizardColors
        @Composable @ReadOnlyComposable get() = LocalCashizardColors.current
    val dimens: Dimens
        @Composable @ReadOnlyComposable get() = LocalDimens.current
    val typography: CashizardTypography
        @Composable @ReadOnlyComposable get() = LocalCashizardTypography.current
}
