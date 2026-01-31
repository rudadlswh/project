package com.crossfit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.crossfit.app.ui.components.AppHeader
import com.crossfit.app.ui.components.OutlinedCard
import com.crossfit.app.ui.viewmodel.WodViewModel
import java.time.LocalDate

@Composable
fun WodEditScreen(wodViewModel: WodViewModel = hiltViewModel()) {
    val focusManager = LocalFocusManager.current
    val today = remember { LocalDate.now() }
    var hydrated by rememberSaveable { mutableStateOf(false) }
    var wodTitle by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var wodType by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var wodDescription by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(today) {
        wodViewModel.load(today)
    }
    LaunchedEffect(wodViewModel.wod) {
        val wod = wodViewModel.wod
        if (!hydrated && wod != null) {
            wodTitle = TextFieldValue(wod.title)
            wodType = TextFieldValue(wod.type)
            wodDescription = TextFieldValue(wod.description)
            hydrated = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
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
                value = wodTitle,
                onValueChange = { wodTitle = it },
                label = { Text("와드 이름") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            OutlinedTextField(
                value = wodType,
                onValueChange = { wodType = it },
                label = { Text("유형") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            OutlinedTextField(
                value = wodDescription,
                onValueChange = { wodDescription = it },
                label = { Text("설명") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
            wodViewModel.errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            wodViewModel.successMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = {
                    wodViewModel.createOrUpdate(
                        today,
                        wodTitle.text.trim(),
                        wodType.text.trim(),
                        wodDescription.text.trim()
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !wodViewModel.isLoading
            ) {
                Text(text = if (wodViewModel.isLoading) "저장 중..." else "저장")
            }
            Button(
                onClick = { wodViewModel.delete(today) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = wodViewModel.wod != null && !wodViewModel.isLoading
            ) {
                Text(text = "삭제")
            }
        }
    }
}
