package com.houseofrafa.cashizard.presentation.common

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn

private val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

/**
 * "July 2026" — the month switcher's label. The year is dropped when it matches
 * the current year, so most of the time it reads simply "July".
 */
fun LocalDate.monthYearLabel(
    today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
): String {
    val name = monthNames[monthNumber - 1]
    return if (year == today.year) name else "$name $year"
}

/** "12 July" — a compact date for transaction subtitles. */
fun LocalDate.dayMonthLabel(): String = "$dayOfMonth ${monthNames[monthNumber - 1]}"

/**
 * The day-section heading: "TODAY" and "YESTERDAY" for the two most recent
 * days, otherwise "15 JULY". Returned uppercase to match the design.
 */
fun LocalDate.daySectionLabel(
    today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
): String = when (this) {
    today -> "TODAY"
    today.minus(DatePeriod(days = 1)) -> "YESTERDAY"
    else -> "$dayOfMonth ${monthNames[monthNumber - 1].uppercase()}"
}
