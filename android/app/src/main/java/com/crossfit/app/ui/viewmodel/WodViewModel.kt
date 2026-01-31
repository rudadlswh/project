package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.CreateWodRequest
import com.crossfit.app.data.model.WodResponse
import com.crossfit.app.data.repo.CrossfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WodViewModel @Inject constructor(
    private val repository: CrossfitRepository
) : ViewModel() {
    var wod by mutableStateOf<WodResponse?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    fun load(date: LocalDate) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                wod = repository.wod(date.toString())
            } catch (e: HttpException) {
                wod = null
                errorMessage = if (e.code() == 404) {
                    "오늘의 와드가 아직 등록되지 않았습니다."
                } else {
                    "와드를 불러오지 못했습니다."
                }
            } catch (_: Exception) {
                wod = null
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    fun createOrUpdate(date: LocalDate, title: String, type: String, description: String) {
        if (title.isBlank() || type.isBlank() || description.isBlank()) {
            errorMessage = "모든 항목을 입력해주세요."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                val res = repository.createWod(
                    CreateWodRequest(
                        date = date.toString(),
                        title = title,
                        type = type,
                        description = description
                    )
                )
                wod = res
                successMessage = "오늘의 와드가 저장되었습니다."
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 401 || e.code() == 403) {
                    "관리자 권한이 필요합니다."
                } else {
                    "와드 저장에 실패했습니다."
                }
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    fun delete(date: LocalDate) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            try {
                repository.deleteWod(date.toString())
                wod = null
                successMessage = "오늘의 와드가 삭제되었습니다."
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 401 || e.code() == 403) {
                    "관리자 권한이 필요합니다."
                } else {
                    "와드 삭제에 실패했습니다."
                }
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
