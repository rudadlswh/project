package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.SessionResponse
import com.crossfit.app.data.model.SessionReservationResponse
import com.crossfit.app.data.model.MyReservationResponse
import com.crossfit.app.data.repo.CrossfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReserveViewModel @Inject constructor(
    private val repository: CrossfitRepository
) : ViewModel() {
    var sessions by mutableStateOf<List<SessionResponse>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isActionLoading by mutableStateOf(false)
        private set
    var actionSessionId by mutableStateOf<Long?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var actionMessage by mutableStateOf<String?>(null)
        private set
    var rosters by mutableStateOf<Map<Long, List<SessionReservationResponse>>>(emptyMap())
        private set
    var rosterLoadingSessionId by mutableStateOf<Long?>(null)
        private set
    var rosterErrorMessage by mutableStateOf<String?>(null)
        private set
    var myReservations by mutableStateOf<List<MyReservationResponse>>(emptyList())
        private set
    var myReservationsLoading by mutableStateOf(false)
        private set

    fun load(date: LocalDate) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                sessions = repository.sessions(date.toString())
                loadMyReservations()
            } catch (e: HttpException) {
                errorMessage = "세션을 불러오지 못했습니다."
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    fun reserve(date: LocalDate, timeSlot: String, sessionId: Long) {
        viewModelScope.launch {
            isActionLoading = true
            actionSessionId = sessionId
            errorMessage = null
            actionMessage = null
            try {
                val res = repository.reserve(date.toString(), timeSlot)
                actionMessage = res.message
                sessions = repository.sessions(date.toString())
                loadMyReservations()
            } catch (e: HttpException) {
                errorMessage = when (e.code()) {
                    400 -> "요청을 확인해주세요."
                    409 -> "이미 예약되어 있습니다."
                    else -> "예약에 실패했습니다."
                }
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isActionLoading = false
                actionSessionId = null
            }
        }
    }

    fun cancel(date: LocalDate, timeSlot: String, sessionId: Long) {
        viewModelScope.launch {
            isActionLoading = true
            actionSessionId = sessionId
            errorMessage = null
            actionMessage = null
            try {
                val res = repository.cancel(date.toString(), timeSlot)
                actionMessage = res.message
                sessions = repository.sessions(date.toString())
                loadMyReservations()
            } catch (e: HttpException) {
                errorMessage = when (e.code()) {
                    400 -> "취소 가능 시간이 지났습니다."
                    404 -> "예약을 찾을 수 없습니다."
                    else -> "취소에 실패했습니다."
                }
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isActionLoading = false
                actionSessionId = null
            }
        }
    }

    fun clearMessages() {
        errorMessage = null
        actionMessage = null
        rosterErrorMessage = null
    }

    fun loadRoster(sessionId: Long) {
        viewModelScope.launch {
            rosterLoadingSessionId = sessionId
            rosterErrorMessage = null
            try {
                val roster = repository.sessionReservations(sessionId)
                rosters = rosters + (sessionId to roster)
            } catch (e: HttpException) {
                rosterErrorMessage = "예약자 명단을 불러오지 못했습니다."
            } catch (_: Exception) {
                rosterErrorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                rosterLoadingSessionId = null
            }
        }
    }

    private suspend fun loadMyReservations() {
        myReservationsLoading = true
        try {
            myReservations = repository.myReservations()
        } catch (_: Exception) {
            // silent for now
        } finally {
            myReservationsLoading = false
        }
    }
}
