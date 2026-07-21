package com.houseofrafa.cashizard.presentation.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Type scale mimicking SF Pro using the platform system font (SF Pro on iOS,
 * Roboto on Android). Amount styles use tabular figures ("tnum") so digits
 * align in columns.
 */
@Immutable
data class CashizardTypography(
    /** Brand wordmark on the login screen. */
    val displayTitle: TextStyle = base(42, FontWeight.Bold, letterSpacing = (-0.5)),
    /** Screen-opening headline, e.g. "Create your account". */
    val displayHeadline: TextStyle = base(30, FontWeight.Bold, letterSpacing = (-0.3)),
    val largeTitle: TextStyle = base(34, FontWeight.Bold, letterSpacing = 0.3),
    val title1: TextStyle = base(28, FontWeight.Bold, letterSpacing = (-0.3)),
    val title2: TextStyle = base(22, FontWeight.Bold),
    val title3: TextStyle = base(20, FontWeight.SemiBold),
    val headline: TextStyle = base(17, FontWeight.SemiBold),
    val body: TextStyle = base(17, FontWeight.Normal),
    /** 16pt regular: wallet rows, inset rows, text inputs. */
    val bodyLarge: TextStyle = base(16, FontWeight.Normal),
    /** 16pt medium: transaction row titles. */
    val rowTitle: TextStyle = base(16, FontWeight.Medium),
    /** 15pt medium: analytics row names. */
    val rowTitleSmall: TextStyle = base(15, FontWeight.Medium),
    val rowSubtitle: TextStyle = base(13, FontWeight.Normal),
    val subhead: TextStyle = base(15, FontWeight.Medium),
    /** 14pt pair used by the auth footers ("Don't have an account? Sign up"). */
    val bodySmall: TextStyle = base(14, FontWeight.Normal),
    val bodySmallStrong: TextStyle = base(14, FontWeight.SemiBold),
    val footnote: TextStyle = base(13, FontWeight.Normal),
    /** 13pt semibold: segmented control labels, pill actions. */
    val footnoteStrong: TextStyle = base(13, FontWeight.SemiBold),
    val caption: TextStyle = base(12, FontWeight.Medium),
    val caption2: TextStyle = base(11, FontWeight.Medium),
    /** Tab bar destination labels. */
    val tabLabel: TextStyle = base(10, FontWeight.Medium),
    /** Uppercase section label on main screens (TODAY, OTHER WALLETS). */
    val sectionLabel: TextStyle = base(13, FontWeight.SemiBold, letterSpacing = 0.5),
    /** The slightly smaller variant used inside sheets (CATEGORY). */
    val sectionLabelSmall: TextStyle = base(12, FontWeight.SemiBold, letterSpacing = 0.5),
    /** Trailing "See all" action next to a section label. */
    val sectionAction: TextStyle = base(13, FontWeight.Medium),

    // Monetary — tabular figures
    val amountHero: TextStyle = tabular(48, FontWeight.Bold, letterSpacing = (-1.0)),
    val amountTotal: TextStyle = tabular(40, FontWeight.Bold, letterSpacing = (-0.5)),
    val amountLarge: TextStyle = tabular(34, FontWeight.Bold, letterSpacing = (-0.5)),
    val amountTitle: TextStyle = tabular(24, FontWeight.Bold),
    val amountHeader: TextStyle = tabular(17, FontWeight.SemiBold),
    val amount: TextStyle = tabular(16, FontWeight.SemiBold),
    val amountSmall: TextStyle = tabular(15, FontWeight.SemiBold),
)

private fun base(
    size: Int,
    weight: FontWeight,
    letterSpacing: Double = 0.0,
): TextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = size.sp,
    fontWeight = weight,
    letterSpacing = letterSpacing.sp,
)

private fun tabular(
    size: Int,
    weight: FontWeight,
    letterSpacing: Double = 0.0,
): TextStyle = base(size, weight, letterSpacing).copy(
    fontFeatureSettings = "tnum",
)
