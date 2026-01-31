package com.crossfit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
fun ProfileScreen(
    onSettingsClick: () -> Unit = {},
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

        Text(
            text = "계정과 회원권을 관리하세요",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedCard {
            Text(
                text = "개인 정보",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            InfoRow(label = "이름", value = "관리자")
            InfoRow(label = "이메일", value = "관리자@짐.kr")
            InfoRow(label = "권한") {
                Tag(
                    text = "관리자",
                    background = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        OutlinedCard {
            Text(
                text = "설정",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "앱 환경설정",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            InfoRow(label = "푸시 알림") {
                Tag(
                    text = "준비중",
                    background = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            }
            InfoRow(label = "이용약관", value = "보기")
        }

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "로그아웃")
        }
    }
}
