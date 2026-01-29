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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.OutlinedCard

@Composable
fun WodEditScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppHeader(
            title = "크로스핏 짐",
            subtitle = "관리자",
            onSettingsClick = null
        )

        Text(
            text = "오늘의 와드 수정",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedCard {
            OutlinedTextField(
                value = "프란",
                onValueChange = {},
                label = { Text("와드 이름") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "타임",
                onValueChange = {},
                label = { Text("유형") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "21-15-9 반복\n- 쓰러스터 (95/65 파운드)\n- 풀업",
                onValueChange = {},
                label = { Text("설명") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "저장")
            }
        }
    }
}
