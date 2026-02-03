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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.crossfit.app.data.model.SessionResponse
import com.crossfit.app.data.model.SessionReservationResponse
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.CalendarDayCell
import com.crossfit.app.ui.components.OutlinedCard
import com.crossfit.app.ui.components.Tag
import com.crossfit.app.ui.viewmodel.ReserveViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ReserveScreen(
    onSettingsClick: () -> Unit = {},
    headerSubtitle: String = "회원",
    isStaff: Boolean = false,
    reserveViewModel: ReserveViewModel = hiltViewModel()
) {
    val today = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(today) }
    var expandedSessionId by remember { mutableStateOf<Long?>(null) }
    val monthLabel = remember(currentMonth) {
        DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN).format(currentMonth.atDay(1))
    }
    val weeks = remember(currentMonth) { buildCalendar(currentMonth) }
    val dayOfWeekKorean = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)

    LaunchedEffect(currentMonth) {
        if (selectedDate.month != currentMonth.month || selectedDate.year != currentMonth.year) {
            selectedDate = currentMonth.atDay(1)
        }
    }
    LaunchedEffect(selectedDate) {
        reserveViewModel.clearMessages()
        reserveViewModel.load(selectedDate)
    }

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
            CalendarHeader(
                monthLabel = monthLabel,
                onPrev = { currentMonth = currentMonth.minusMonths(1) },
                onNext = { currentMonth = currentMonth.plusMonths(1) }
            )
            WeekdayRow()
            CalendarGrid(
                weeks = weeks,
                selectedDay = selectedDate.dayOfMonth,
                onDaySelected = { day -> selectedDate = currentMonth.atDay(day) }
            )
        }

        OutlinedCard {
            Text(
                text = "예약 가능한 수업",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${selectedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN))} $dayOfWeekKorean",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            reserveViewModel.errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            reserveViewModel.actionMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when {
                reserveViewModel.isLoading -> {
                    Text(
                        text = "세션을 불러오는 중...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                reserveViewModel.sessions.isEmpty() -> {
                    Text(
                        text = "해당 날짜에 세션이 없습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    reserveViewModel.sessions.forEach { session ->
                        if (isStaff) {
                            StaffSessionRow(
                                session = session,
                                expanded = expandedSessionId == session.id,
                                onToggle = {
                                    expandedSessionId = if (expandedSessionId == session.id) null else session.id
                                    if (expandedSessionId == session.id &&
                                        !reserveViewModel.rosters.containsKey(session.id)
                                    ) {
                                        reserveViewModel.loadRoster(session.id)
                                    }
                                }
                            )
                            if (expandedSessionId == session.id) {
                                val roster = reserveViewModel.rosters[session.id].orEmpty()
                                RosterList(
                                    roster = roster,
                                    isLoading = reserveViewModel.rosterLoadingSessionId == session.id,
                                    errorMessage = reserveViewModel.rosterErrorMessage
                                )
                            }
                        } else {
                            SessionRow(
                                session = session,
                                isStaff = false,
                                isActionLoading = reserveViewModel.isActionLoading &&
                                    reserveViewModel.actionSessionId == session.id,
                                onReserve = {
                                    reserveViewModel.reserve(selectedDate, session.timeSlot, session.id)
                                },
                                onCancel = {
                                    reserveViewModel.cancel(selectedDate, session.timeSlot, session.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (!isStaff) {
            OutlinedCard {
                Text(
                    text = "내 예약",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (reserveViewModel.myReservationsLoading) {
                    Text(
                        text = "내 예약을 불러오는 중...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (reserveViewModel.myReservations.isEmpty()) {
                    Text(
                        text = "예약 내역이 없습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    reserveViewModel.myReservations
                        .sortedBy { it.date + it.timeSlot }
                        .forEach { item ->
                            MyReservationRow(item)
                        }
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    monthLabel: String,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
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
        CalendarNavButton("<", onPrev)
        Spacer(modifier = Modifier.width(8.dp))
        CalendarNavButton(">", onNext)
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
private fun SessionRow(
    session: SessionResponse,
    isStaff: Boolean,
    isActionLoading: Boolean,
    onReserve: () -> Unit,
    onCancel: () -> Unit
) {
    val myStatus = session.myStatus
    val isReserved = myStatus == "RESERVED"
    val isWaitlist = myStatus == "WAITLIST"
    val capacityText = session.capacity?.let { "정원 ${it}명" } ?: "정원 무제한"
    val countText = buildString {
        append("예약 ${session.bookedCount}명")
        if (session.waitlistCount > 0) {
            append(" / 대기 ${session.waitlistCount}명")
        }
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = session.timeSlot,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = capacityText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = countText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
    if (!isStaff && (isReserved || isWaitlist)) {
        val statusText = if (isReserved) {
            "예약됨"
        } else {
                        val pos = session.myWaitlistPosition?.let { " ${it}번" } ?: ""
                        "대기$pos"
                    }
                    Tag(
                        text = statusText,
                        background = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (!isStaff) {
                val label = when {
                    isReserved -> "예약 취소"
                    isWaitlist -> "대기 취소"
                    else -> "예약"
                }
                val colors = if (isReserved || isWaitlist) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Button(
                    onClick = if (isReserved || isWaitlist) onCancel else onReserve,
                    colors = colors,
                    enabled = !isActionLoading
                ) {
                    Text(text = if (isActionLoading) "처리 중..." else label)
                }
            }
        }
    }
}

@Composable
private fun StaffSessionRow(
    session: SessionResponse,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val capacityText = session.capacity?.let { "정원 ${it}명" } ?: "정원 무제한"
    val countText = buildString {
        append("예약 ${session.bookedCount}명")
        if (session.waitlistCount > 0) {
            append(" / 대기 ${session.waitlistCount}명")
        }
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = session.timeSlot,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = capacityText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = countText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (expanded) "접기" else "보기",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RosterList(
    roster: List<SessionReservationResponse>,
    isLoading: Boolean,
    errorMessage: String?
) {
    if (isLoading) {
        Text(
            text = "예약자 명단을 불러오는 중...",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, top = 6.dp, bottom = 6.dp)
        )
        return
    }
    if (!errorMessage.isNullOrBlank()) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 8.dp, top = 6.dp, bottom = 6.dp)
        )
        return
    }
    if (roster.isEmpty()) {
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
        roster.forEach { item ->
            val statusLabel = when (item.status) {
                "RESERVED" -> "예약"
                "WAITLIST" -> "대기"
                else -> item.status
            }
            Text(
                text = "- ${item.displayName} ($statusLabel)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MyReservationRow(item: com.crossfit.app.data.model.MyReservationResponse) {
    val statusLabel = when (item.status) {
        "RESERVED" -> "예약"
        "WAITLIST" -> "대기"
        "CANCELED" -> "취소"
        else -> item.status
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${item.date} ${item.timeSlot}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = statusLabel,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
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
