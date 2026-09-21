package com.mits.subscription.presenatation.ui.auth

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.BuildConfig
import com.mits.subscription.data.auth.GoogleAuthManager
import com.mits.subscription.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleAuthManager: GoogleAuthManager,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
    val userEmail: StateFlow<String?> = authRepository.userEmail

    fun signIn(activityContext: Context) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            runCatching {
                val idToken = googleAuthManager.requestGoogleIdToken(
                    activityContext,
                    BuildConfig.GOOGLE_SERVER_CLIENT_ID,
                )
                authRepository.loginWithGoogle(idToken, deviceLabel = Build.MODEL).getOrThrow()
            }.onSuccess {
                _state.update { it.copy(isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
