package com.mits.subscription.presenatation.ui.shared

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.data.repo.FileHandler
import com.mits.subscription.data.repo.PaymentFile
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Subscription
import com.mits.subscription.model.Workshop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel
@Inject constructor(
    private val repository: SubscriptionRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val fileHandler: FileHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sharedUri = savedStateHandle["sharedUri"] ?: ""

    private val _uiState = MutableStateFlow<SharedState>(SharedState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        repository.workshops.flowOn(Dispatchers.IO)
            .filterNotNull()
            .onEach {
                _uiState.update { currentState ->
                    when (currentState) {
                        is SharedState.Success -> currentState.copy(workshops = transformToUiState(it))
                        is SharedState.Loading -> SharedState.Success(
                            workshops = transformToUiState(it),
                            paymentFile = null
                        )

                        SharedState.Finish -> currentState
                    }
                }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch(ioDispatcher) {
            if (sharedUri.isNotEmpty()) {
                val uri = fileHandler.handleFile(sharedUri)
                _uiState.update { currentState ->
                    when (currentState) {
                        is SharedState.Success -> currentState.copy(paymentFile = uri)
                        is SharedState.Loading -> SharedState.Success(
                            workshops = emptyList(),
                            paymentFile = uri
                        )

                        SharedState.Finish -> {
                            SharedState.Finish
                        }
                    }
                }
            }
        }
    }


    fun addFileToSubscription(subscription: Subscription) {
        viewModelScope.launch(ioDispatcher) {
            val paymentFile: PaymentFile? = if (_uiState.value is SharedState.Success) {
                (_uiState.value as SharedState.Success).paymentFile
            } else {
                null
            }
            paymentFile?.let {
                repository.updatePaymentFileInfo(
                    subscriptionId = subscription.id,
                    uri = it.uri.toString(),
                    fileName = it.name,
                )
            }
            _uiState.update { SharedState.Finish }
        }
    }

    private fun transformToUiState(workshops: List<Workshop>): List<WorkShopUiState> {
        return workshops.map { workshop ->
            val list = workshop.subscriptions.sortedByDescending { it.startDate }
            WorkShopUiState(
                name = workshop.name,
                currentSubscription = list.firstOrNull(),
                old = list.drop(1)
            )
        }
    }

    sealed class SharedState {
        data class Success(
            val workshops: List<WorkShopUiState>,
            val paymentFile: PaymentFile?
        ) : SharedState()

        data object Loading : SharedState()

        data object Finish : SharedState()
    }
}

data class WorkShopUiState(
    val name: String,
    val currentSubscription: Subscription? = null,
    val old: List<Subscription> = emptyList()
)