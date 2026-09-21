package com.mits.subscription.presenatation.ui.auth

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.mits.subscription.BuildConfig
import com.mits.subscription.data.auth.GoogleAuthManager
import com.mits.subscription.data.sync.SyncPreferences
import com.mits.subscription.domain.repository.AuthRepository
import com.mits.subscription.presenatation.worker.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val googleAuthManager: GoogleAuthManager,
    private val authRepository: AuthRepository,
    syncPreferences: SyncPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
    val userEmail: StateFlow<String?> = authRepository.userEmail

    val lastSyncCompletedAt: StateFlow<Long?> = syncPreferences.lastSyncCompletedAt
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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

    fun syncNow() {
        SyncWorker.triggerNow(WorkManager.getInstance(appContext))
    }
}
