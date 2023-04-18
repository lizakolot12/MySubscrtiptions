package com.mits.subscription.ui.creating

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.R
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.getDefaultDetail
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreatingViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val viewModelState = mutableStateOf(CreatingState())

    init {
        val defaultDetail = getDefaultDetail()
        viewModelState.value = CreatingState(defaultDetail)
    }

    val uiState = viewModelState
    fun create(
        name: String,
        detail: String? = null,
        lessonNumbers: Int,
        startDate: Date,
        endDate: Date
    ) {
        val copy = CreatingState()
        copy.isLoading = true
        viewModelState.value = copy
        viewModelScope.launch {
            val workshopId = repository.createWorkshop(name)
            val newSubscription = Subscription(
                -1,
                detail = detail,
                startDate = startDate,
                endDate = endDate,
                lessonNumbers = lessonNumbers,
                workshopId = workshopId,
                message = null
            )
            repository.createSubscription(newSubscription)
            val newState = CreatingState()
            newState.isLoading = false
            newState.finished = true
            viewModelState.value = newState
        }
    }

    fun checkName(name: String) {
        if (name.isBlank()) {
            viewModelState.value.nameError = R.string.name_error
        } else {
            viewModelState.value.nameError = null
        }
        val newState = viewModelState.value
        newState.name = name
        newState.defaultDetailStrId = null
        viewModelState.value = newState
        checkSave()
    }

    private fun checkSave() {
        val currentState = CreatingState(viewModelState.value.defaultDetailStrId)
        currentState.name = viewModelState.value.name
        currentState.detail = viewModelState.value.detail
        currentState.number = viewModelState.value.number
        currentState.startDate = viewModelState.value.startDate
        currentState.endDate = viewModelState.value.endDate
        currentState.nameError = viewModelState.value.nameError
        currentState.savingAvailable = getSaveAvailability(viewModelState.value)
        currentState.finished = viewModelState.value.finished
        currentState.generalError = viewModelState.value.generalError
        currentState.isLoading = viewModelState.value.isLoading
        viewModelState.value = currentState
    }

    private fun getSaveAvailability(currentState: CreatingState): Boolean {
        return currentState.nameError == null &&
                currentState.generalError == null
                && !currentState.isLoading
    }

    fun checkDetail(text: String) {
        if (text.isNotBlank()) {
            val newState = viewModelState.value
            newState.detail = text
            newState.defaultDetailStrId = null
            viewModelState.value = newState
        }
    }

    fun acceptStartDate(date:Calendar) {
            val newState = viewModelState.value
            newState.startDate = date
            viewModelState.value = newState
    }

    fun acceptEndDate(date:Calendar) {
        val newState = viewModelState.value
        newState.endDate = date
        viewModelState.value = newState
    }

    fun acceptNumber(number:Int) {
        val newState = viewModelState.value
        newState.number = number
        viewModelState.value = newState
    }

    class CreatingState(var defaultDetailStrId: Int? = null) {
        var name:String = ""
        var detail:String = ""
        var number:Int = 0
        var startDate:Calendar = Calendar.getInstance()
        var endDate:Calendar = Calendar.getInstance()
        var nameError: Int? = null
        var savingAvailable: Boolean = false
        var finished: Boolean = false
        var generalError: Int? = null
        var isLoading: Boolean = false
    }
}