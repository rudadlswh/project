package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.CreateCoachRequest
import com.crossfit.app.data.model.ExtendMembershipRequest
import com.crossfit.app.data.repo.CrossfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: CrossfitRepository
) : ViewModel() {
    var isCoachLoading by mutableStateOf(false)
        private set
    var coachErrorMessage by mutableStateOf<String?>(null)
        private set
    var coachSuccessMessage by mutableStateOf<String?>(null)
        private set

    var isMembershipLoading by mutableStateOf(false)
        private set
    var membershipErrorMessage by mutableStateOf<String?>(null)
        private set
    var membershipSuccessMessage by mutableStateOf<String?>(null)
        private set

    fun registerCoach(displayName: String, email: String) {
        if (displayName.isBlank() || email.isBlank()) {
            coachErrorMessage = "이름과 이메일을 입력해주세요."
            return
        }
        viewModelScope.launch {
            isCoachLoading = true
            coachErrorMessage = null
            coachSuccessMessage = null
            try {
                repository.createCoach(CreateCoachRequest(email.trim(), displayName.trim()))
                coachSuccessMessage = "코치 계정이 생성되었습니다. 초기 비밀번호는 1234입니다."
            } catch (e: HttpException) {
                coachErrorMessage = when (e.code()) {
                    401, 403 -> "관리자 권한이 필요합니다."
                    409 -> "이미 존재하는 이메일입니다."
                    400 -> "입력 정보를 확인해주세요."
                    else -> "코치 등록에 실패했습니다."
                }
            } catch (_: Exception) {
                coachErrorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isCoachLoading = false
            }
        }
    }

    fun extendMembership(query: String, daysInput: String) {
        if (query.isBlank()) {
            membershipErrorMessage = "회원 이름 또는 이메일을 입력해주세요."
            return
        }
        val days = daysInput.trim().toIntOrNull()
        if (days == null || days <= 0) {
            membershipErrorMessage = "연장 일수를 올바르게 입력해주세요."
            return
        }
        viewModelScope.launch {
            isMembershipLoading = true
            membershipErrorMessage = null
            membershipSuccessMessage = null
            try {
                val res = repository.extendMembership(ExtendMembershipRequest(query.trim(), days))
                membershipSuccessMessage = res.endDate?.let {
                    "회원권이 연장되었습니다. 종료일: $it"
                } ?: "회원권이 연장되었습니다."
            } catch (e: HttpException) {
                membershipErrorMessage = when (e.code()) {
                    401, 403 -> "관리자 권한이 필요합니다."
                    404 -> "회원을 찾을 수 없습니다."
                    409 -> "동일한 이름의 회원이 여러 명입니다. 이메일로 검색해주세요."
                    400 -> "요청이 올바르지 않습니다."
                    else -> "연장에 실패했습니다."
                }
            } catch (_: Exception) {
                membershipErrorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isMembershipLoading = false
            }
        }
    }

    fun clearCoachMessages() {
        coachErrorMessage = null
        coachSuccessMessage = null
    }

    fun clearMembershipMessages() {
        membershipErrorMessage = null
        membershipSuccessMessage = null
    }
}
