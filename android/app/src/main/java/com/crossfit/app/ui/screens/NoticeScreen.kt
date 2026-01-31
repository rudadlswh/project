package com.crossfit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.OutlinedCard
import com.crossfit.app.ui.components.Tag
import com.crossfit.app.ui.viewmodel.NoticeViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NoticeScreen(
    onSettingsClick: () -> Unit = {},
    headerSubtitle: String = "회원",
    isAdmin: Boolean = false,
    onCreateNoticeClick: () -> Unit = {},
    noticeViewModel: NoticeViewModel = hiltViewModel()
) {
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

        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "공지사항",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isAdmin) {
                androidx.compose.material3.TextButton(onClick = onCreateNoticeClick) {
                    Text(text = "작성")
                }
            }
        }
        Text(
            text = "최신 소식과 정보를 확인하세요",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when {
            noticeViewModel.isListLoading -> {
                OutlinedCard {
                    Text(
                        text = "공지사항을 불러오는 중...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            !noticeViewModel.listErrorMessage.isNullOrBlank() -> {
                OutlinedCard {
                    Text(
                        text = noticeViewModel.listErrorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            noticeViewModel.notices.isEmpty() -> {
                OutlinedCard {
                    Text(
                        text = "등록된 공지사항이 없습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                noticeViewModel.notices.forEachIndexed { index, notice ->
                    OutlinedCard {
                        AnnouncementCard(
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
private fun AnnouncementCard(
    title: String,
    date: String,
    pinned: Boolean,
    body: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
