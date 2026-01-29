package com.crossfit.app.data.repo

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor() {
    var accessToken: String? = null
}
