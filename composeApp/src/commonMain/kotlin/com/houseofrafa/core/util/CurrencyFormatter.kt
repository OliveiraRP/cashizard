package com.houseofrafa.core.util

/** Formats this [Double] as a currency string, e.g. "€1,234.56". */
fun Double.formatAsCurrency(): String {
    val negative = this < 0
    val abs      = if (negative) -this else this
    val euros  = abs.toLong()
    val cents    = ((abs - euros) * 100 + 0.5).toLong().coerceIn(0, 99)

    val eurosStr = buildString {
        val s = euros.toString()
        s.forEachIndexed { i, c ->
            if (i > 0 && (s.length - i) % 3 == 0) append(',')
            append(c)
        }
    }

    val prefix = if (negative) "-€" else "€"
    return "$prefix$eurosStr.${cents.toString().padStart(2, '0')}"
}
