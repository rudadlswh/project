package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.BulkRecordRequest
import com.crossfit.app.data.repo.CrossfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RecordBulkViewModel @Inject constructor(
    private val repository: CrossfitRepository
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    fun submit(members: List<String>, recordType: String, value: String, recordDate: String, wodTitle: String) {
        if (members.isEmpty()) {
            errorMessage = "회원 목록을 입력해주세요."
            return
        }
        if (recordType.isBlank() || value.isBlank() || recordDate.isBlank() || wodTitle.isBlank()) {
            errorMessage = "모든 항목을 입력해주세요."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                val res = repository.bulkRecords(
                    BulkRecordRequest(
                        members = members,
                        recordType = recordType,
                        value = value,
                        recordDate = recordDate,
                        wodTitle = wodTitle
                    )
                )
                successMessage = if (res.failedMembers.isEmpty()) {
                    res.message
                } else {
                    "${res.message} (실패: ${res.failedMembers.joinToString(", ")})"
                }
            } catch (e: HttpException) {
                errorMessage = "일괄 등록에 실패했습니다."
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}
