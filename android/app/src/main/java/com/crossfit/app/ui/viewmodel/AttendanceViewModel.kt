package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.AttendanceSummaryResponse
import com.crossfit.app.data.repo.CrossfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: CrossfitRepository
) : ViewModel() {
    var summary by mutableStateOf<AttendanceSummaryResponse?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun load(month: YearMonth) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                summary = repository.monthlyAttendance(month.toString())
            } catch (e: HttpException) {
                errorMessage = "출석 정보를 불러오지 못했습니다."
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }
}
