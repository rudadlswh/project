package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.CreateNoticeRequest
import com.crossfit.app.data.model.NoticeResponse
import com.crossfit.app.data.repo.CrossfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val repository: CrossfitRepository
) : ViewModel() {
    var notices by mutableStateOf<List<NoticeResponse>>(emptyList())
        private set
    var isListLoading by mutableStateOf(false)
        private set
    var listErrorMessage by mutableStateOf<String?>(null)
        private set

    var latestNotice by mutableStateOf<NoticeResponse?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    fun create(title: String, content: String) {
        if (title.isBlank() || content.isBlank()) {
            errorMessage = "제목과 내용을 모두 입력해주세요."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                val res = repository.createNotice(
                    CreateNoticeRequest(
                        title = title,
                        content = content
                    )
                )
                latestNotice = res
                successMessage = "공지사항이 등록되었습니다."
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 401 || e.code() == 403) {
                    "관리자 권한이 필요합니다."
                } else {
                    "공지사항 등록에 실패했습니다."
                }
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    fun loadNotices() {
        viewModelScope.launch {
            isListLoading = true
            listErrorMessage = null
            try {
                notices = repository.notices()
            } catch (e: HttpException) {
                listErrorMessage = "공지사항을 불러오지 못했습니다."
            } catch (_: Exception) {
                listErrorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isListLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}
