package com.houseofrafa.cashizard.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.houseofrafa.cashizard.presentation.designsystem.theme.CashizardTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

private val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

/** Weekday initials, Monday-first to match the European locale the app targets. */
private val weekdayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

/**
 * An inline month calendar in the iOS graphical style: a month header with
 * navigation chevrons, a Monday-first weekday row, and a grid of day cells. The
 * selected day is a filled accent disc; today is accent-tinted text.
 *
 * It keeps its own visible-month state so paging does not disturb the selection;
 * picking a day reports it through [onSelect].
 */
@Composable
fun InlineDatePicker(
    selected: LocalDate,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors
    val dimens = CashizardTheme.dimens
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    // Reset to the selected day's month whenever the selection changes, so the
    // grid always opens on the chosen day even after external edits.
    var visibleMonth by remember(selected) {
        mutableStateOf(LocalDate(selected.year, selected.monthNumber, 1))
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = dimens.space12, vertical = dimens.space8)) {
        // Header: month + year with paging chevrons.
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.space4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${monthNames[visibleMonth.monthNumber - 1]} ${visibleMonth.year}",
                style = CashizardTheme.typography.headline,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            MonthChevron(Lucide.ChevronLeft, "Previous month") {
                visibleMonth = visibleMonth.minus(DatePeriod(months = 1))
            }
            Spacer(Modifier.size(dimens.space20))
            MonthChevron(Lucide.ChevronRight, "Next month") {
                visibleMonth = visibleMonth.plus(DatePeriod(months = 1))
            }
        }

        Spacer(Modifier.size(dimens.space8))

        // Weekday header.
        Row(Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { label ->
                Text(
                    text = label,
                    style = CashizardTheme.typography.caption2,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.size(dimens.space4))

        // Day grid. Leading blanks pad the first week to the correct weekday.
        val firstOfMonth = visibleMonth
        val daysInMonth = firstOfMonth.daysUntil(firstOfMonth.plus(DatePeriod(months = 1)))
        val leadingBlanks = firstOfMonth.dayOfWeek.isoDayNumber - 1
        val cells: List<LocalDate?> =
            List(leadingBlanks) { null } +
                (0 until daysInMonth).map { firstOfMonth.plus(DatePeriod(days = it)) }

        cells.chunked(DAYS_PER_WEEK).forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        date = date,
                        isSelected = date == selected,
                        isToday = date == today,
                        onClick = { date?.let(onSelect) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // Pad a short final week so its cells keep the grid's column width.
                repeat(DAYS_PER_WEEK - week.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate?,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CashizardTheme.colors

    Box(
        modifier = modifier.aspectRatio(1f).padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (date == null) return@Box

        val textColor = when {
            isSelected -> colors.onAccent
            isToday -> colors.accent
            else -> colors.textPrimary
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .then(if (isSelected) Modifier.background(colors.accent) else Modifier)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = CashizardTheme.typography.subhead.copy(
                    fontWeight = if (isSelected || isToday) FontWeight.SemiBold else FontWeight.Normal,
                ),
                color = textColor,
            )
        }
    }
}

@Composable
private fun MonthChevron(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = CashizardTheme.colors.accent,
        modifier = Modifier
            .size(22.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    )
}

private const val DAYS_PER_WEEK = 7
