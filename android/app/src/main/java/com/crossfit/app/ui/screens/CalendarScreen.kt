package com.crossfit.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.CalendarDayCell
import com.crossfit.app.ui.components.InfoRow
import com.crossfit.app.ui.components.OutlinedCard
import com.crossfit.app.ui.viewmodel.AttendanceViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    onSettingsClick: () -> Unit = {},
    isStaff: Boolean = false,
    headerSubtitle: String = "회원",
    attendanceViewModel: AttendanceViewModel = hiltViewModel()
) {
    val today = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val monthLabel = remember(currentMonth) {
        DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN).format(currentMonth.atDay(1))
    }
    val weeks = remember(currentMonth) { buildCalendar(currentMonth) }

    LaunchedEffect(currentMonth) {
        attendanceViewModel.load(currentMonth)
    }

    val summary = attendanceViewModel.summary
    val attendedDays = summary?.dates
        ?.mapNotNull { runCatching { LocalDate.parse(it).dayOfMonth }.getOrNull() }
        ?.toSet()
        ?: emptySet()
    val attendanceRate = summary?.attendanceRate?.let { String.format(Locale.KOREAN, "%.1f%%", it * 100) } ?: "-"
    val totalDays = summary?.totalDays?.toString() ?: "-"
    val weekdays = summary?.weekdaysInMonth?.toString() ?: "-"
    val todaySelectedDay = if (currentMonth == YearMonth.from(today)) today.dayOfMonth else -1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppHeader(
            title = "크로스핏 짐",
            subtitle = headerSubtitle,
            onSettingsClick = onSettingsClick
        )

        if (!isStaff) {
            Text(
                text = "출석 캘린더",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "운동 출석을 확인하세요",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedCard {
                Text(
                    text = "월간 통계",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = monthLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (attendanceViewModel.errorMessage != null) {
                    Text(
                        text = attendanceViewModel.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (attendanceViewModel.isLoading) {
                    Text(
                        text = "출석 정보를 불러오는 중...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                InfoRow(label = "출석률", value = attendanceRate)
                InfoRow(label = "출석 일수", value = totalDays)
                InfoRow(label = "평일 일수", value = weekdays)
                Text(
                    text = "* 출석률 = 출석일 / 평일(월-금) 기준",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = monthLabel,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    CalendarNavButton("<") { currentMonth = currentMonth.minusMonths(1) }
                    Spacer(modifier = Modifier.width(8.dp))
                    CalendarNavButton(">") { currentMonth = currentMonth.plusMonths(1) }
                }
                WeekdayRow()
                CalendarGrid(
                    weeks = weeks,
                    todayDay = todaySelectedDay,
                    attendedDays = attendedDays
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LegendItem(label = "오늘", color = MaterialTheme.colorScheme.primary)
                    LegendItem(label = "출석", color = MaterialTheme.colorScheme.onSurface)
                    LegendItem(label = "주말", color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun CalendarNavButton(label: String, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = color,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Box(modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeekdayRow() {
    val labels = listOf("일", "월", "화", "수", "목", "금", "토")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        labels.forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    weeks: List<List<CalendarDayCell>>,
    todayDay: Int,
    attendedDays: Set<Int>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { cell ->
                    val isToday = cell.inMonth && cell.day == todayDay
                    val isAttended = cell.inMonth && attendedDays.contains(cell.day)
                    val background = when {
                        isToday -> MaterialTheme.colorScheme.primary
                        isAttended -> MaterialTheme.colorScheme.onSurface
                        else -> Color.Transparent
                    }
                    val border = if (cell.inMonth) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    }
                    Surface(
                        shape = CircleShape,
                        color = background,
                        border = border,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = cell.day.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = when {
                                    isToday -> MaterialTheme.colorScheme.onPrimary
                                    isAttended -> MaterialTheme.colorScheme.surface
                                    cell.inMonth -> MaterialTheme.colorScheme.onSurface
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildCalendar(month: YearMonth): List<List<CalendarDayCell>> {
    val first = month.atDay(1)
    val offset = first.dayOfWeek.value % 7
    val daysInMonth = month.lengthOfMonth()
    val totalCells = ((offset + daysInMonth + 6) / 7) * 7
    val prevMonth = month.minusMonths(1)
    val prevMonthDays = prevMonth.lengthOfMonth()
    val cells = (0 until totalCells).map { index ->
        val dayNumber = index - offset + 1
        when {
            dayNumber < 1 -> CalendarDayCell(prevMonthDays + dayNumber, false)
            dayNumber > daysInMonth -> CalendarDayCell(dayNumber - daysInMonth, false)
            else -> CalendarDayCell(dayNumber, true)
        }
    }
    return cells.chunked(7)
}
