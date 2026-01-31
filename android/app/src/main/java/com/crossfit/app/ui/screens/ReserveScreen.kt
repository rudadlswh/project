package com.crossfit.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.CalendarDayCell
import com.crossfit.app.ui.components.OutlinedCard
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ReserveScreen(
    onSettingsClick: () -> Unit = {},
    headerSubtitle: String = "회원",
    isStaff: Boolean = false
) {
    var selectedDay by remember { mutableStateOf(29) }
    var expandedSlot by remember { mutableStateOf<String?>(null) }
    val slots = slotsForDay(selectedDay)
    val selectedDate = LocalDate.of(2026, 1, selectedDay)
    val dayOfWeekKorean = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)

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

        Text(
            text = "수업 예약",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "날짜와 시간대를 선택하세요",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedCard {
            Text(
                text = "날짜 선택",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            CalendarHeader(monthLabel = "2026년 1월")
            WeekdayRow()
            CalendarGrid(
                weeks = sampleCalendarWeeks(),
                selectedDay = selectedDay,
                onDaySelected = { day ->
                    selectedDay = day
                    expandedSlot = null
                }
            )
        }

        OutlinedCard {
            Text(
                text = "예약 가능한 수업",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "2026년 1월 ${selectedDay}일 $dayOfWeekKorean",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            slots.forEach { slot ->
                val expanded = expandedSlot == slot.time
                ClassSlotRow(
                    slot = slot,
                    isStaff = isStaff,
                    expanded = expanded,
                    onToggle = {
                        expandedSlot = if (expanded) null else slot.time
                    }
                )
                if (isStaff && expanded) {
                    ReservationList(names = slot.reservedBy)
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(monthLabel: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthLabel,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                modifier = Modifier.size(26.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "<", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                modifier = Modifier.size(26.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = ">", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
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
    onDaySelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { cell ->
                    val isSelected = cell.inMonth && cell.day == selectedDay
                    val background = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    val border = if (cell.inMonth) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    }
                    Surface(
                        shape = CircleShape,
                        color = background,
                        border = border,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(enabled = cell.inMonth) { onDaySelected(cell.day) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = cell.day.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
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

@Composable
private fun ClassSlotRow(
    slot: ReservationSlot,
    isStaff: Boolean,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isStaff, onClick = onToggle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = slot.time,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isStaff) "예약 ${slot.reservedBy.size}명" else "예약 가능",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (isStaff) {
                Text(
                    text = if (expanded) "접기" else "보기",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 6.dp
                    )
                ) {
                    Text(text = "예약")
                }
            }
        }
    }
}

@Composable
private fun ReservationList(names: List<String>) {
    if (names.isEmpty()) {
        Text(
            text = "예약자가 없습니다",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, top = 6.dp, bottom = 6.dp)
        )
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 6.dp, bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        names.forEach { name ->
            Text(
                text = "- $name",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class ReservationSlot(
    val time: String,
    val reservedBy: List<String>
)

private fun slotsForDay(day: Int): List<ReservationSlot> {
    val baseTimes = listOf("09:00", "10:30", "17:30", "19:00", "20:30")
    val sample = mapOf(
        29 to listOf(
            ReservationSlot("09:00", listOf("김민지", "박도현", "이서준")),
            ReservationSlot("10:30", listOf("최하린")),
            ReservationSlot("17:30", listOf("윤지후", "한지수")),
            ReservationSlot("19:00", listOf("오서진")),
            ReservationSlot("20:30", emptyList())
        ),
        30 to listOf(
            ReservationSlot("09:00", listOf("정우성")),
            ReservationSlot("10:30", emptyList()),
            ReservationSlot("17:30", listOf("조하윤", "유민서")),
            ReservationSlot("19:00", emptyList()),
            ReservationSlot("20:30", listOf("김도윤"))
        )
    )
    val slots = sample[day]
    if (slots != null) return slots
    return baseTimes.map { time -> ReservationSlot(time, emptyList()) }
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
