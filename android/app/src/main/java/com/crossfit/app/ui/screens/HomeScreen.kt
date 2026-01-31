package com.crossfit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.InfoRow
import com.crossfit.app.ui.components.OutlinedCard
import com.crossfit.app.ui.components.Tag
import com.crossfit.app.ui.viewmodel.NoticeViewModel
import com.crossfit.app.ui.viewmodel.WodViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onTodayWodClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    isStaff: Boolean = false,
    headerSubtitle: String = "회원",
    isAdmin: Boolean = false,
    onEditWodClick: () -> Unit = {},
    onCreateNoticeClick: () -> Unit = {},
    displayName: String? = null,
    wodViewModel: WodViewModel = hiltViewModel(),
    noticeViewModel: NoticeViewModel = hiltViewModel()
) {
    val today = remember { LocalDate.now() }
    val formattedDate = remember(today) {
        DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN).format(today)
    }
    LaunchedEffect(today) {
        wodViewModel.load(today)
    }
    LaunchedEffect(Unit) {
        noticeViewModel.loadNotices()
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
            text = "다시 오신 것을 환영합니다, ${displayName ?: headerSubtitle}님!",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (!isStaff) {
            OutlinedCard {
                Text(
                    text = "내 회원권",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                InfoRow(label = "종류", value = "기간제")
                InfoRow(label = "상태") {
                    Tag(
                        text = "만료",
                        background = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                }
                InfoRow(label = "잔여", value = "만료")
            }
        }

        OutlinedCard {
            Text(
                text = "오늘의 와드",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (wodViewModel.isLoading) {
                Text(
                    text = "오늘의 와드를 불러오는 중...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (wodViewModel.wod == null) {
                Text(
                    text = wodViewModel.errorMessage ?: "오늘의 와드가 아직 등록되지 않았습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "오늘의 운동",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = wodViewModel.wod?.title.orEmpty(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Tag(
                        text = wodViewModel.wod?.type.orEmpty(),
                        background = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = wodViewModel.wod?.description.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onTodayWodClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "자세히 보기")
                }
                if (isAdmin) {
                    TextButton(onClick = onEditWodClick) {
                        Text(text = "수정")
                    }
                }
            }
        }

        OutlinedCard {
            Row {
                Text(
                    text = "공지사항",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                if (isAdmin) {
                    TextButton(onClick = onCreateNoticeClick) {
                        Text(text = "작성")
                    }
                }
            }
            when {
                noticeViewModel.isListLoading -> {
                    Text(
                        text = "공지사항을 불러오는 중...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                !noticeViewModel.listErrorMessage.isNullOrBlank() -> {
                    Text(
                        text = noticeViewModel.listErrorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                noticeViewModel.notices.isEmpty() -> {
                    Text(
                        text = "등록된 공지사항이 없습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    noticeViewModel.notices.take(2).forEachIndexed { index, notice ->
                        AnnouncementItem(
                            title = notice.title,
                            date = formatNoticeDate(notice.createdAt, notice.createdBy),
                            pinned = index == 0,
                            body = notice.content
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementItem(
    title: String,
    date: String,
    pinned: Boolean,
    body: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (pinned) {
                Tag(
                    text = "고정",
                    background = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
    }
}

private fun formatNoticeDate(createdAt: String, createdBy: String): String {
    return try {
        val parsed = LocalDateTime.parse(createdAt)
        val formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h:mm", Locale.KOREAN)
        "${formatter.format(parsed)} · ${createdBy}"
    } catch (_: Exception) {
        "작성자: $createdBy"
    }
}
