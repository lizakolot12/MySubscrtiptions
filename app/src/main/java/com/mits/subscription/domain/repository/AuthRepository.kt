package com.mits.subscription.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val isLoggedIn: StateFlow<Boolean>

    val userEmail: StateFlow<String?>

    suspend fun loginWithGoogle(idToken: String, deviceLabel: String?): Result<Unit>

    suspend fun logout()
}
