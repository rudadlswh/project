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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.InfoRow
import com.crossfit.app.ui.components.OutlinedCard
import com.crossfit.app.ui.components.Tag

@Composable
fun HomeScreen(
    onTodayWodClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    isStaff: Boolean = false,
    headerSubtitle: String = "회원",
    isAdmin: Boolean = false,
    onEditWodClick: () -> Unit = {},
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

        Text(
            text = "다시 오신 것을 환영합니다, 관리자님!",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "2026년 1월 29일 목요일",
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
            Text(
                text = "오늘의 운동",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "프란",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Tag(
                    text = "타임",
                    background = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "21-15-9 반복 (타임)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "- 쓰러스터 (95/65 파운드)\n- 풀업",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
            AnnouncementItem(
                title = "크로스핏 짐에 오신 것을 환영합니다!",
                date = "2025년 1월 20일",
                pinned = true,
                body = "커뮤니티에 함께해 주셔서 감사합니다."
            )
            AnnouncementItem(
                title = "새 수업 일정",
                date = "2025년 1월 25일",
                pinned = false,
                body = "다음 주부터 오전 6시 추가 수업이 시작됩니다."
            )
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
