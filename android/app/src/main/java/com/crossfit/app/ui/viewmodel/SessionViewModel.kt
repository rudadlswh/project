package com.crossfit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.crossfit.app.data.repo.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val tokenStore: TokenStore
) : ViewModel() {
    val role = tokenStore.role
    val displayName = tokenStore.displayName

    fun logout() {
        tokenStore.clear()
    }
}
