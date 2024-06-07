package com.mits.subscription.ui.creating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.R
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.getDefaultDetail
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CreatingViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreatingState())
    val uiState: StateFlow<CreatingState> = viewModelState

    private val ioDispatcher = Dispatchers.IO

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
                message = null
            )
            repository.createSubscription(newSubscription)
            viewModelState.value = viewModelState.value.copy(isLoading = false, finished = true)
        }
    }

    fun checkName(name: String) {
        viewModelState.value = viewModelState.value.copy(
            nameError = if (name.isBlank()) R.string.name_error else null,
            name = name,
            savingAvailable = name.isNotBlank()
        )
    }


    fun checkDetail(text: String) {
        viewModelState.value = viewModelState.value.copy(
            detail = text
        )
    }

    fun acceptStartDate(date: Calendar) {
        viewModelState.value = viewModelState.value.copy(
            startDate = date
        )
    }

    fun acceptEndDate(date: Calendar) {
        viewModelState.value = viewModelState.value.copy(
            endDate = date
        )
    }

    fun acceptNumber(number: Int) {
        viewModelState.value = viewModelState.value.copy(
            number = number
        )
    }

    data class CreatingState(
        val defaultDetailStrId: Int = getDefaultDetail(),
        var name: String = "",
        var detail: String = "",
        var number: Int = 0,
        var startDate: Calendar = Calendar.getInstance(),
        var endDate: Calendar = Calendar.getInstance(),
        var nameError: Int? = null,
        var savingAvailable: Boolean = false,
        var finished: Boolean = false,
        var generalError: Int? = null,
        var isLoading: Boolean = false
    )

}