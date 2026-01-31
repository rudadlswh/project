package com.crossfit.app.data.repo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor() {
    private val _accessToken = MutableStateFlow<String?>(null)
    private val _role = MutableStateFlow<String?>(null)
    private val _displayName = MutableStateFlow<String?>(null)

    val accessToken: String?
        get() = _accessToken.value

    val role: StateFlow<String?> = _role.asStateFlow()
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    fun update(accessToken: String, role: String, displayName: String) {
        _accessToken.value = accessToken
        _role.value = role
        _displayName.value = displayName
    }

    fun clear() {
        _accessToken.value = null
        _role.value = null
        _displayName.value = null
    }
}
