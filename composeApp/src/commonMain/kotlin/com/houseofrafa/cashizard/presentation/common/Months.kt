package com.houseofrafa.cashizard.presentation.common

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/** The first day of the month containing today, in the device's time zone. */
fun currentMonthStart(): LocalDate {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return LocalDate(today.year, today.monthNumber, 1)
}

/** The last day of the month this date starts, for an inclusive query range. */
fun LocalDate.endOfMonth(): LocalDate =
    plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))

fun LocalDate.previousMonth(): LocalDate = minus(DatePeriod(months = 1))

fun LocalDate.nextMonth(): LocalDate = plus(DatePeriod(months = 1))
