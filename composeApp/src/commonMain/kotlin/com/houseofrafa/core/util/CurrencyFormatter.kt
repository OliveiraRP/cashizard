package com.houseofrafa.core.util

/** Formats this [Double] as a currency string, e.g. "$1,234.56". */
fun Double.formatAsCurrency(): String {
    val negative = this < 0
    val abs      = if (negative) -this else this
    val dollars  = abs.toLong()
    val cents    = ((abs - dollars) * 100 + 0.5).toLong().coerceIn(0, 99)

    val dollarsStr = buildString {
        val s = dollars.toString()
        s.forEachIndexed { i, c ->
            if (i > 0 && (s.length - i) % 3 == 0) append(',')
            append(c)
        }
    }

    val prefix = if (negative) "-$" else "$"
    return "$prefix$dollarsStr.${cents.toString().padStart(2, '0')}"
}
