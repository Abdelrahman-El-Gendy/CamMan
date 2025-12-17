package com.gndy.camman.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gndy.camman.presentation.theme.Gold
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun DateTimePicker(
    selectedDate: LocalDate?,
    selectedTime: LocalTime?,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    availableTimeSlots: List<LocalTime> = getDefaultTimeSlots()
) {
    var currentMonth by remember {
        mutableStateOf(
            selectedDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Calendar Section
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select Date",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Month Navigation
            MonthNavigator(
                currentMonth = currentMonth,
                onPreviousMonth = {
                    currentMonth = currentMonth.plus(-1, DateTimeUnit.MONTH)
                },
                onNextMonth = {
                    currentMonth = currentMonth.plus(1, DateTimeUnit.MONTH)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Day of Week Headers
            DayOfWeekHeaders()

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                minDate = minDate,
                onDateSelected = onDateSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Time Selection Section
            AnimatedVisibility(visible = selectedDate != null) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Time",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TimeSlotGrid(
                        timeSlots = availableTimeSlots,
                        selectedTime = selectedTime,
                        onTimeSelected = onTimeSelected
                    )
                }
            }

            // Selected Summary
            AnimatedVisibility(visible = selectedDate != null && selectedTime != null) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Gold.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Gold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Selected",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "${formatDate(selectedDate!!)} at ${
                                        formatTime(
                                            selectedTime!!
                                        )
                                    }",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Gold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthNavigator(
    currentMonth: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Month"
            )
        }

        Text(
            text = "${getMonthName(currentMonth.month)} ${currentMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Month"
            )
        }
    }
}

@Composable
private fun DayOfWeekHeaders() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: LocalDate,
    selectedDate: LocalDate?,
    minDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = getDaysInMonth(currentMonth)
    val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.month, 1)
    val startingDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal
    val adjustedStartDay =
        if (startingDayOfWeek == 6) 0 else startingDayOfWeek + 1 // Adjust for Sunday start

    val totalCells = adjustedStartDay + daysInMonth
    val rows = (totalCells + 6) / 7

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height((rows * 44).dp),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Empty cells before first day
        items(adjustedStartDay) {
            Box(modifier = Modifier.aspectRatio(1f))
        }

        // Days of the month
        items(daysInMonth) { index ->
            val day = index + 1
            val date = LocalDate(currentMonth.year, currentMonth.month, day)
            val isSelected = selectedDate == date
            val isPast = date < minDate
            val isToday =
                date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            DayCell(
                day = day,
                isSelected = isSelected,
                isPast = isPast,
                isToday = isToday,
                onClick = { if (!isPast) onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isPast: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> Gold
            isToday -> Gold.copy(alpha = 0.2f)
            else -> Color.Transparent
        }
    )

    val textColor = when {
        isSelected -> Color.Black
        isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(1.dp, Gold, CircleShape)
                } else Modifier
            )
            .clickable(enabled = !isPast, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
private fun TimeSlotGrid(
    timeSlots: List<LocalTime>,
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit
) {
    // Group time slots by period (Morning, Afternoon, Evening)
    val morningSlots = timeSlots.filter { it.hour < 12 }
    val afternoonSlots = timeSlots.filter { it.hour in 12..16 }
    val eveningSlots = timeSlots.filter { it.hour > 16 }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (morningSlots.isNotEmpty()) {
            TimeSlotSection(
                title = "Morning",
                slots = morningSlots,
                selectedTime = selectedTime,
                onTimeSelected = onTimeSelected
            )
        }
        if (afternoonSlots.isNotEmpty()) {
            TimeSlotSection(
                title = "Afternoon",
                slots = afternoonSlots,
                selectedTime = selectedTime,
                onTimeSelected = onTimeSelected
            )
        }
        if (eveningSlots.isNotEmpty()) {
            TimeSlotSection(
                title = "Evening",
                slots = eveningSlots,
                selectedTime = selectedTime,
                onTimeSelected = onTimeSelected
            )
        }
    }
}

@Composable
private fun TimeSlotSection(
    title: String,
    slots: List<LocalTime>,
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slots.forEach { time ->
                val isSelected = selectedTime == time
                FilterChip(
                    selected = isSelected,
                    onClick = { onTimeSelected(time) },
                    label = {
                        Text(
                            text = formatTime(time),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Gold,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
    }
}

// Helper Functions
private fun getDefaultTimeSlots(): List<LocalTime> {
    return listOf(
        LocalTime(9, 0),
        LocalTime(10, 0),
        LocalTime(11, 0),
        LocalTime(13, 0),
        LocalTime(14, 0),
        LocalTime(15, 0),
        LocalTime(16, 0),
        LocalTime(17, 0)
    )
}

private fun getDaysInMonth(date: LocalDate): Int {
    return when (date.month) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31

        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeapYear(date.year)) 29 else 28
        else -> 30
    }
}

private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

private fun getMonthName(month: Month): String {
    return when (month) {
        Month.JANUARY -> "January"
        Month.FEBRUARY -> "February"
        Month.MARCH -> "March"
        Month.APRIL -> "April"
        Month.MAY -> "May"
        Month.JUNE -> "June"
        Month.JULY -> "July"
        Month.AUGUST -> "August"
        Month.SEPTEMBER -> "September"
        Month.OCTOBER -> "October"
        Month.NOVEMBER -> "November"
        Month.DECEMBER -> "December"
        else -> ""
    }
}

fun formatDate(date: LocalDate): String {
    val dayOfWeek = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "Mon"
        DayOfWeek.TUESDAY -> "Tue"
        DayOfWeek.WEDNESDAY -> "Wed"
        DayOfWeek.THURSDAY -> "Thu"
        DayOfWeek.FRIDAY -> "Fri"
        DayOfWeek.SATURDAY -> "Sat"
        DayOfWeek.SUNDAY -> "Sun"
        else -> ""
    }
    return "$dayOfWeek, ${getMonthName(date.month)} ${date.dayOfMonth}, ${date.year}"
}

fun formatTime(time: LocalTime): String {
    val hour = if (time.hour > 12) time.hour - 12 else if (time.hour == 0) 12 else time.hour
    val amPm = if (time.hour >= 12) "PM" else "AM"
    val minute = time.minute.toString().padStart(2, '0')
    return "$hour:$minute $amPm"
}
