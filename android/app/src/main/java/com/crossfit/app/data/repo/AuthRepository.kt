package com.crossfit.app.data.repo

import com.crossfit.app.data.api.ApiService
import com.crossfit.app.data.model.AuthResponse
import com.crossfit.app.data.model.LoginRequest
import com.crossfit.app.data.model.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenStore: TokenStore
) {
    suspend fun login(email: String, password: String): AuthResponse {
        val res = apiService.login(LoginRequest(email, password))
        tokenStore.accessToken = res.accessToken
        return res
    }

    suspend fun register(email: String, password: String, displayName: String): AuthResponse {
        val res = apiService.register(RegisterRequest(email, password, displayName))
        tokenStore.accessToken = res.accessToken
        return res
    }
}
