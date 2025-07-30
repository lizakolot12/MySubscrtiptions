package com.mits.subscription.ui.creating

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.R
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.getDefaultDetail
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CreatingViewModel
@Inject constructor(
    private val repository: SubscriptionRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreatingState())
    val uiState: StateFlow<CreatingState> = viewModelState

    init {
        viewModelState.value = CreatingState()
    }

    fun create() {
        viewModelState.value = viewModelState.value.copy(isLoading = true)
        viewModelScope.launch(ioDispatcher) {
            val workshopId = repository.createWorkshop(uiState.value.name)
            val newSubscription = Subscription(
                -1,
                detail = uiState.value.detail,
                startDate = uiState.value.startDate.time,
                endDate = uiState.value.endDate.time,
                lessonNumbers = uiState.value.number,
                workshopId = workshopId,
                message = null,
                filePath = uiState.value.photoUri?.toString()
            )
            repository.createSubscription(newSubscription)
            viewModelState.value = viewModelState.value.copy(isLoading = false, finished = true)
        }
    }

    fun checkName(name: String) {
        viewModelState.update {
            it.copy(
                nameError = if (name.isBlank()) R.string.name_error else null,
                name = name,
                savingAvailable = name.isNotBlank()
            )
        }
    }


    fun checkDetail(text: String) {
        viewModelState.update {
            it.copy(detail = text)
        }
    }

    fun acceptStartDate(date: Calendar) {
        viewModelState.update {
            it.copy(startDate = date)
        }
    }

    fun acceptEndDate(date: Calendar) {
        viewModelState.update {
            it.copy(endDate = date)
        }
    }

    fun acceptNumber(number: Int) {
        viewModelState.update {
            it.copy(number = number)
        }
    }

    fun acceptPhotoUri(photoUri: String?) {
        Log.e("TEST", "Photo URI accepted: $photoUri")
        viewModelState.update { it.copy(photoUri = photoUri) }
    }

    data class CreatingState(
        val defaultDetailStrId: Int = getDefaultDetail(),
        var name: String = "",
        var detail: String = "",
        var number: Int = 0,
        val startDate: Calendar = Calendar.getInstance(),
        val endDate: Calendar = Calendar.getInstance(),
        val nameError: Int? = null,
        val savingAvailable: Boolean = false,
        val finished: Boolean = false,
        val generalError: Int? = null,
        val photoUri: String? = null,
        var isLoading: Boolean = false
    )

}