package com.crossfit.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.crossfit.app.ui.viewmodel.AdminViewModel
import com.crossfit.app.ui.viewmodel.WodViewModel
import java.time.LocalDate

@Composable
fun AdminToolsScreen(
    adminViewModel: AdminViewModel = hiltViewModel(),
    wodViewModel: WodViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val today = remember { LocalDate.now() }
    var coachName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var coachEmail by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var memberQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var extensionDays by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var todayWodTitle by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var todayWodType by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var todayWodDescription by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var wodHydrated by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(today) {
        wodViewModel.load(today)
    }
    LaunchedEffect(wodViewModel.wod) {
        val wod = wodViewModel.wod
        if (!wodHydrated && wod != null) {
            todayWodTitle = TextFieldValue(wod.title)
            todayWodType = TextFieldValue(wod.type)
            todayWodDescription = TextFieldValue(wod.description)
            wodHydrated = true
        }
    }
    LaunchedEffect(adminViewModel.coachSuccessMessage) {
        if (!adminViewModel.coachSuccessMessage.isNullOrBlank()) {
            coachName = TextFieldValue("")
            coachEmail = TextFieldValue("")
        }
    }
    LaunchedEffect(adminViewModel.membershipSuccessMessage) {
        if (!adminViewModel.membershipSuccessMessage.isNullOrBlank()) {
            memberQuery = TextFieldValue("")
            extensionDays = TextFieldValue("")
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
            text = "관리자 도구",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedCard {
            Text(
                text = "코치 등록",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = coachName,
                onValueChange = { coachName = it },
                label = { Text("코치 이름") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            OutlinedTextField(
                value = coachEmail,
                onValueChange = { coachEmail = it },
                label = { Text("이메일") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
            adminViewModel.coachErrorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            adminViewModel.coachSuccessMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = {
                    adminViewModel.registerCoach(
                        displayName = coachName.text.trim(),
                        email = coachEmail.text.trim()
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !adminViewModel.isCoachLoading
            ) {
                Text(text = if (adminViewModel.isCoachLoading) "등록 중..." else "등록")
            }
        }

        OutlinedCard {
            Text(
                text = "회원권 기간 연장",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = memberQuery,
                onValueChange = { memberQuery = it },
                label = { Text("회원 이름 또는 이메일") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            OutlinedTextField(
                value = extensionDays,
                onValueChange = { extensionDays = it },
                label = { Text("연장 일수") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            adminViewModel.membershipErrorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            adminViewModel.membershipSuccessMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = {
                    adminViewModel.extendMembership(
                        query = memberQuery.text.trim(),
                        daysInput = extensionDays.text
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = !adminViewModel.isMembershipLoading
            ) {
                Text(text = if (adminViewModel.isMembershipLoading) "연장 중..." else "연장 적용")
            }
        }

        OutlinedCard {
            Text(
                text = "오늘의 와드 등록",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = todayWodTitle,
                onValueChange = { todayWodTitle = it },
                label = { Text("와드 이름") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            OutlinedTextField(
                value = todayWodType,
                onValueChange = { todayWodType = it },
                label = { Text("유형") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            OutlinedTextField(
                value = todayWodDescription,
                onValueChange = { todayWodDescription = it },
                label = { Text("설명") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
            val actionError = wodViewModel.errorMessage
                ?.takeIf { it != "오늘의 와드가 아직 등록되지 않았습니다." }
            actionError?.let {
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
                        todayWodTitle.text.trim(),
                        todayWodType.text.trim(),
                        todayWodDescription.text.trim()
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !wodViewModel.isLoading
            ) {
                Text(text = if (wodViewModel.isLoading) "등록 중..." else "등록")
            }
        }
    }
}
