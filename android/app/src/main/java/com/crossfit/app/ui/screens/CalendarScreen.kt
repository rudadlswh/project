package com.crossfit.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.CalendarDayCell
import com.crossfit.app.ui.components.InfoRow
import com.crossfit.app.ui.components.OutlinedCard

@Composable
fun CalendarScreen(
    onSettingsClick: () -> Unit = {},
    isStaff: Boolean = false,
    headerSubtitle: String = "회원"
) {
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
                    text = "2026년 1월",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                InfoRow(label = "출석률", value = "0.0%")
                InfoRow(label = "출석 일수", value = "0")
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
                        text = "2026년 1월",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    CalendarNavButton("<")
                    Spacer(modifier = Modifier.width(8.dp))
                    CalendarNavButton(">")
                }
                WeekdayRow()
                CalendarGrid(
                    weeks = sampleCalendarWeeks(),
                    selectedDay = 29,
                    attendedDays = setOf(2, 5, 7, 9, 12, 14, 16, 19, 21)
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
private fun CalendarNavButton(label: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier.size(26.dp),
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
    selectedDay: Int,
    attendedDays: Set<Int>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { cell ->
                    val isSelected = cell.inMonth && cell.day == selectedDay
                    val isAttended = cell.inMonth && attendedDays.contains(cell.day)
                    val background = when {
                        isSelected -> MaterialTheme.colorScheme.primary
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
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
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

private fun sampleCalendarWeeks(): List<List<CalendarDayCell>> {
    return listOf(
        listOf(
            CalendarDayCell(28, false),
            CalendarDayCell(29, false),
            CalendarDayCell(30, false),
            CalendarDayCell(31, false),
            CalendarDayCell(1, true),
            CalendarDayCell(2, true),
            CalendarDayCell(3, true)
        ),
        listOf(
            CalendarDayCell(4, true),
            CalendarDayCell(5, true),
            CalendarDayCell(6, true),
            CalendarDayCell(7, true),
            CalendarDayCell(8, true),
            CalendarDayCell(9, true),
            CalendarDayCell(10, true)
        ),
        listOf(
            CalendarDayCell(11, true),
            CalendarDayCell(12, true),
            CalendarDayCell(13, true),
            CalendarDayCell(14, true),
            CalendarDayCell(15, true),
            CalendarDayCell(16, true),
            CalendarDayCell(17, true)
        ),
        listOf(
            CalendarDayCell(18, true),
            CalendarDayCell(19, true),
            CalendarDayCell(20, true),
            CalendarDayCell(21, true),
            CalendarDayCell(22, true),
            CalendarDayCell(23, true),
            CalendarDayCell(24, true)
        ),
        listOf(
            CalendarDayCell(25, true),
            CalendarDayCell(26, true),
            CalendarDayCell(27, true),
            CalendarDayCell(28, true),
            CalendarDayCell(29, true),
            CalendarDayCell(30, true),
            CalendarDayCell(31, true)
        )
    )
}
