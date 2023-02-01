package com.mits.subscription.ui.detail

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(
    private val repository: SubscriptionRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailState(null, null))
    val uiState = _uiState.asStateFlow()
    private val subscriptionId: Long

    init {
        subscriptionId = state.get<Long>("subscriptionId") ?: 0L
        _uiState.value = DetailState(null, null)
        viewModelScope.launch {
            withContext(Dispatchers.IO) { updateCurrentState() }
        }
    }

    private suspend fun updateCurrentState() {
        val subscription = repository.getSubscription(subscriptionId)
        val workshop = repository.getWorkshop(subscription.workshopId)
        val newState = DetailState(subscription, workshop.name)
        val error = if (workshop.name.isBlank()) {
            R.string.name_error
        } else {
            null
        }
        newState.nameError = error
        _uiState.value = newState
    }

    fun deleteLesson(lesson: Lesson) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLesson(lesson)
            updateCurrentState()
        }
    }

    fun acceptNameWorkshop(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = currentStateCopy()
            repository.updateWorkshop(
                currentState.subscription?.workshopId ?: -1,
                name
            )
            updateCurrentState()
        }
    }

    fun acceptDetail(detail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSubscription = uiState.value.subscription
            currentSubscription?.detail = detail
            currentSubscription?.let { repository.update(currentSubscription) }
            updateCurrentState()
        }
    }

    fun acceptNumber(numStr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentSubscription = _uiState.value.subscription
                currentSubscription?.lessonNumbers = numStr.toInt()
                currentSubscription?.let { repository.update(currentSubscription) }
                updateCurrentState()
            } catch (ex: Exception) {
                val newState = currentStateCopy()
                newState.generalError = ex.message
                _uiState.value = newState
            }
        }

    }

    private fun copy(subscription: Subscription?): Subscription? {
        return subscription?.let {
            Subscription(
                it.id,
                subscription.detail,
                subscription.startDate,
                subscription.endDate,
                subscription.lessonNumbers,
                subscription.lessons,
                subscription.workshopId
            )
        }
    }

    private fun currentStateCopy(): DetailState {
        val state = uiState.value
        val newState = DetailState(state.subscription, state.workshopName)
        newState.nameError = state.nameError
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
            viewModelScope.launch(Dispatchers.IO) {
                subscription?.let { repository.update(subscription) }
                updateCurrentState()
            }
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
            viewModelScope.launch(Dispatchers.IO) {
                subscription?.let { repository.update(subscription) }
                updateCurrentState()
            }
        } catch (ex: Exception) {
            val newState = currentStateCopy()
            newState.generalError = ex.message
            _uiState.value = newState
        }
    }

    fun addVisitedLesson() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLesson(subscriptionId, Lesson(-1, "", Date()))
            updateCurrentState()
        }
    }

    fun changeLessonDate(item: Lesson, newCalendar: Calendar) {
        viewModelScope.launch(Dispatchers.IO) {
            item.date = newCalendar.time
            repository.updateLesson(item, newCalendar, subscriptionId)
            updateCurrentState()
        }

    }

    data class DetailState(
        var subscription: Subscription?, var workshopName: String?,
        var nameError: Int? = null,
        var finished: Boolean = false,
        var generalError: String? = null,
        var isLoading: Boolean = false
    )
}