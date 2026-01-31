package com.crossfit.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crossfit.app.data.model.LoginRequest
import com.crossfit.app.data.repo.CrossfitRepository
import com.crossfit.app.data.repo.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: CrossfitRepository,
    private val tokenStore: TokenStore
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "이메일과 비밀번호를 입력해주세요."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val res = repository.login(LoginRequest(email, password))
                tokenStore.update(res.accessToken, res.role, res.displayName)
                onSuccess()
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 401 || e.code() == 403) {
                    "로그인에 실패했습니다."
                } else {
                    "서버 오류가 발생했습니다."
                }
            } catch (_: Exception) {
                errorMessage = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }
}
