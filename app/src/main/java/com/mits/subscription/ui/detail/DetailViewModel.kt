package com.mits.subscription.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.R
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailState(null, null))
    val uiState = _uiState.asStateFlow()

    fun init(id: Long?) {
        _uiState.value = DetailState(null, null)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val subscription = repository.getSubscription(id ?: 0)
                val workshop = repository.getWorkshop(subscription.workshopId)
                val newState = DetailState(subscription, workshop.name)
                _uiState.value = newState
            }
        }
    }

    private fun updateCurrentState(state: DetailState) {
        state.savingAvailable = isSavingAvailability(state)
        _uiState.update { state }
    }

    fun deleteLesson(lesson: Lesson) {
        val subscription = copy(uiState.value.subscription)
        val array = subscription?.lessons?.toMutableList()
        array?.remove(lesson)
        subscription?.lessons = array
        val currentState = currentStateCopy()
        currentState.subscription = subscription
        updateCurrentState(currentState)
    }

    fun save() {
        val newState = DetailState(_uiState.value.subscription, uiState.value.workshopName)
        newState.isLoading = true
        _uiState.value = newState
        viewModelScope.launch {
            val endedState = DetailState(_uiState.value.subscription, uiState.value.workshopName)
            uiState.value.subscription?.let { repository.update(it, uiState.value.workshopName) }
            endedState.isLoading = false
            endedState.finished = true
            _uiState.value = endedState
        }
    }

    fun checkNameWorkshop(name: String) {
        val error = if (name.isBlank()) {
            R.string.name_error
        } else {
            null
        }
        val currentState = currentStateCopy()
        currentState.workshopName = name
        currentState.nameError = error
        updateCurrentState(currentState)
    }

    fun acceptDetail(detail: String) {
        val currentState = currentStateCopy()
        val subscription = copy(currentState.subscription)
        subscription?.detail = detail
        currentState.subscription = subscription
        updateCurrentState(currentState)
    }

    fun acceptNumber(numStr: String) {
        try {
            val currentState = currentStateCopy()
            val subscription = copy(currentState.subscription)
            subscription?.lessonNumbers = numStr.toInt()
            currentState.subscription = subscription
            updateCurrentState(currentState)
        } catch (ex: Exception) {
            val newState = currentStateCopy()
            newState.generalError = ex.message
            _uiState.value = newState
        }
    }

    private fun copy(subscription: Subscription?): Subscription?{
        return subscription?.let {
            Subscription(
                it.id, subscription.detail,subscription.startDate,subscription.endDate,subscription.lessonNumbers,
            subscription.lessons,subscription.workshopId)
        }
    }

    private fun currentStateCopy(): DetailState {
        val state = uiState.value
        val newState = DetailState(state.subscription, state.workshopName)
        newState.nameError = state.nameError
        newState.savingAvailable = state.savingAvailable
        newState.finished = state.finished
        newState.generalError = state.generalError
        newState.isLoading = state.isLoading
        return newState
    }

    fun acceptStartCalendar(calendar: Calendar) {
        try {
            val subscription = copy(uiState.value.subscription)
            subscription?.startDate = calendar.time
            if ((subscription?.endDate ?: Date()) < (subscription?.startDate ?: Date())) {
                subscription?.endDate = subscription?.startDate
            }
            val currentState = currentStateCopy()
            currentState.subscription = subscription
            updateCurrentState(currentState)
        } catch (ex: Exception) {
            val newState = currentStateCopy()
            newState.generalError = ex.message
            _uiState.value = newState
        }
    }

    fun acceptEndCalendar(calendar: Calendar) {
        try {
            val subscription = copy(uiState.value.subscription)
            subscription?.endDate = calendar.time
            val currentState = currentStateCopy()
            currentState.subscription = subscription
            updateCurrentState(currentState)
        } catch (ex: Exception) {
            val newState = currentStateCopy()
            newState.generalError = ex.message
            _uiState.value = newState
        }
    }

    fun addVisitedLesson() {
        viewModelScope.launch {
            val subscription = copy(uiState.value.subscription)
            val array = subscription?.lessons?.toMutableList()
            array?.add(Lesson(-1, "", Date()))
            subscription?.lessons = array
            val currentState = currentStateCopy()
            currentState.subscription = subscription
            updateCurrentState(currentState)
        }
    }

    private fun isSavingAvailability(state: DetailState): Boolean {
        return state.nameError == null
                && !state.isLoading
    }

    fun changeLessonDate(item: Lesson, newCalendar: Calendar) {
        val subscription = uiState.value.subscription
        val array = subscription?.lessons?.toMutableList()
        array?.forEach {
            if (item.lId == it.lId) {
                item.date = newCalendar.time
            }
        }
        subscription?.lessons = array
        val currentState = currentStateCopy()
        currentState.subscription = subscription
        updateCurrentState(currentState)
    }

    data class DetailState(var subscription: Subscription?, var workshopName: String?,
        var nameError: Int? = null,
        var savingAvailable: Boolean = true,
        var finished: Boolean = false,
        var generalError: String? = null,
        var isLoading: Boolean = false
    )
}