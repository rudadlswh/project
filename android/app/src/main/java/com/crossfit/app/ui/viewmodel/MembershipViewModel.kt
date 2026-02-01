package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.MembershipResponse
import com.crossfit.app.data.repo.CrossfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MembershipViewModel @Inject constructor(
    private val repository: CrossfitRepository
) : ViewModel() {
    var membership by mutableStateOf<MembershipResponse?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun load() {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                membership = repository.membership()
            } catch (e: HttpException) {
                membership = null
                errorMessage = when (e.code()) {
                    401, 403 -> "로그인이 필요합니다."
                    404 -> "회원권 정보가 없습니다."
                    else -> "회원권 정보를 불러오지 못했습니다."
                }
            } catch (_: Exception) {
                membership = null
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }
}
