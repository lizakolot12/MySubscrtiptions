package com.mits.subscription.data.repository

import com.mits.subscription.data.auth.TokenStore
import com.mits.subscription.data.remote.AuthApi
import com.mits.subscription.data.remote.dto.GoogleAuthRequest
import com.mits.subscription.data.remote.dto.RefreshRequest
import com.mits.subscription.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
) : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(tokenStore.accessToken != null)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow(tokenStore.userEmail)
    override val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    override suspend fun loginWithGoogle(idToken: String, deviceLabel: String?): Result<Unit> =
        runCatching {
            val response = authApi.loginWithGoogle(GoogleAuthRequest(idToken, deviceLabel))
            tokenStore.save(response.accessToken, response.refreshToken, response.email)
            _isLoggedIn.value = true
            _userEmail.value = response.email
        }

    override suspend fun logout() {
        val refreshToken = tokenStore.refreshToken
        tokenStore.clear()
        _isLoggedIn.value = false
        _userEmail.value = null
        if (refreshToken != null) {
            // Best-effort: the server-side token is already useless to the app either way once cleared locally.
            runCatching { authApi.logout(RefreshRequest(refreshToken)) }
        }
    }
}
