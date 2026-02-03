package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.CreateRecordRequest
import com.crossfit.app.data.model.RecordResponse
import com.crossfit.app.data.repo.CrossfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val repository: CrossfitRepository
) : ViewModel() {
    var records by mutableStateOf<List<RecordResponse>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    fun load() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                records = repository.myRecords()
            } catch (e: HttpException) {
                errorMessage = "기록을 불러오지 못했습니다."
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    fun createRecord(wodId: Long?, type: String, value: String, date: LocalDate) {
        createRecordWithImage(wodId, type, value, date, null)
    }

    fun createRecordWithImage(
        wodId: Long?,
        type: String,
        value: String,
        date: LocalDate,
        imagePayload: ImagePayload?
    ) {
        if (value.isBlank()) {
            errorMessage = "기록 값을 입력해주세요."
            return
        }
        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            successMessage = null
            try {
                val imageUrl = if (imagePayload != null) {
                    repository.uploadImage(
                        bytes = imagePayload.bytes,
                        filename = imagePayload.filename,
                        mimeType = imagePayload.mimeType
                    ).url
                } else {
                    null
                }
                repository.createRecord(
                    CreateRecordRequest(
                        wodId = wodId,
                        type = type,
                        value = value,
                        recordDate = date.toString(),
                        imageUrl = imageUrl
                    )
                )
                successMessage = "기록이 저장되었습니다."
                records = repository.myRecords()
            } catch (e: HttpException) {
                errorMessage = "기록 저장에 실패했습니다."
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isSaving = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}

data class ImagePayload(
    val bytes: ByteArray,
    val filename: String,
    val mimeType: String?
)
