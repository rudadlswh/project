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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.OutlinedCard
import com.crossfit.app.ui.components.Tag

@Composable
fun NoticeScreen(
    onSettingsClick: () -> Unit = {},
    headerSubtitle: String = "회원",
    isAdmin: Boolean = false,
    onCreateNoticeClick: () -> Unit = {}
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

        OutlinedCard {
            AnnouncementCard(
                title = "크로스핏 짐에 오신 것을 환영합니다!",
                date = "2025년 1월 20일 오전 9:00",
                pinned = true,
                body = "커뮤니티에 함께해 주셔서 감사합니다. 첫 수업은 10분 일찍 도착해주세요."
            )
        }

        OutlinedCard {
            AnnouncementCard(
                title = "새 수업 일정",
                date = "2025년 1월 25일 오전 9:00",
                pinned = false,
                body = "다음 주부터 오전 6시 추가 수업이 시작됩니다!"
            )
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
