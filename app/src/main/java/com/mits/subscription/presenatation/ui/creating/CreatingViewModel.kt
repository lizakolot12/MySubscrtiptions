package com.mits.subscription.presenatation.ui.creating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.R
import com.mits.subscription.data.repo.FileHandler
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Subscription
import com.mits.subscription.presenatation.ui.creating.data.CreatingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CreatingViewModel
@Inject constructor(
    private val repository: SubscriptionRepository,
    private val fileHandler: FileHandler,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val creatingState = MutableStateFlow(
        CreatingState(startDate = Calendar.getInstance().timeInMillis,
        endDate = Calendar.getInstance().timeInMillis)
    )
    val uiState: StateFlow<CreatingState> = creatingState

    init {
        creatingState.value = CreatingState(startDate = Calendar.getInstance().timeInMillis,
            endDate = Calendar.getInstance().timeInMillis)
    }

    fun create() {
        creatingState.value = creatingState.value.copy(isLoading = true)
        viewModelScope.launch(ioDispatcher) {
            val workshopId = repository.createWorkshop(uiState.value.name)
            val newSubscription = Subscription(
                -1,
                detail = uiState.value.detail,
                startDate = uiState.value.startDate,
                endDate = uiState.value.endDate,
                lessonNumbers = uiState.value.number,
                workshopId = workshopId,
                message = null,
                filePath = uiState.value.fileUri?.uri?.toString(),
                originFileName = uiState.value.fileUri?.name,
            )
            repository.createSubscription(newSubscription)
            creatingState.value = creatingState.value.copy(isLoading = false, finished = true)
        }
    }

    fun checkName(name: String) {
        creatingState.update {
            it.copy(
                nameError = if (name.isBlank()) R.string.name_error else null,
                name = name,
                savingAvailable = name.isNotBlank()
            )
        }
    }


    fun checkDetail(text: String) {
        creatingState.update {
            it.copy(detail = text)
        }
    }

    fun acceptStartDate(date: Long) {
        creatingState.update {
            it.copy(startDate = date)
        }
    }

    fun acceptEndDate(date: Long) {
        creatingState.update {
            it.copy(endDate = date)
        }
    }

    fun acceptNumber(number: Int) {
        creatingState.update {
            it.copy(number = number)
        }
    }

    fun acceptUri(uri: String?) {
        viewModelScope.launch {
            creatingState.update {
                val fileUri =  withContext(ioDispatcher) {
                    uri?.let { fileHandler.handleFile(it) }
                }
                it.copy(fileUri = fileUri)
            }
        }
    }

}