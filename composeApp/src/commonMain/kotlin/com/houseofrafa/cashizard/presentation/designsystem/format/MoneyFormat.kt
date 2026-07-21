package com.houseofrafa.cashizard.presentation.designsystem.format

import com.houseofrafa.cashizard.domain.model.Money

/**
 * The single money formatter for the app. European EUR style: '.' thousands
 * separator, ',' decimal separator, trailing "€" — e.g. `1.234,56 €`.
 *
 * Negatives use a true minus sign (U+2212) rather than a hyphen, matching the
 * design and aligning better with the tabular figures.
 *
 * @param withSign when true, always prefixes '+' for non-negative amounts
 *        (negatives always show their minus).
 * @param showCents when false, drops the decimals for round amounts display.
 */
fun Money.formatEur(
    withSign: Boolean = false,
    showCents: Boolean = true,
): String {
    val whole = groupThousands(wholeEuros)
    val body = if (showCents) {
        "$whole,${remainderCents.toString().padStart(2, '0')}"
    } else {
        whole
    }
    val sign = when {
        isNegative -> "−"
        withSign -> "+"
        else -> ""
    }
    return "$sign$body €"
}

private fun groupThousands(value: Long): String {
    val digits = value.toString()
    if (digits.length <= 3) return digits
    val sb = StringBuilder()
    val firstGroup = digits.length % 3
    if (firstGroup > 0) {
        sb.append(digits, 0, firstGroup)
        if (digits.length > firstGroup) sb.append('.')
    }
    var i = firstGroup
    while (i < digits.length) {
        sb.append(digits, i, i + 3)
        if (i + 3 < digits.length) sb.append('.')
        i += 3
    }
    return sb.toString()
}
