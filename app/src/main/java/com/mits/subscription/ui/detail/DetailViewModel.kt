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
        val error = if (workshop.name.isBlank()) {
            R.string.name_error
        } else {
            null
        }
        val newState = DetailState(subscription, workshop.name, nameError = error)
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
            val currentState = _uiState.value
            repository.updateWorkshop(
                currentState.subscription?.workshopId ?: -1,
                name
            )
            updateCurrentState()
        }
    }

    fun acceptDetail(detail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSubscription = copyAndUpdate(uiState.value.subscription, detail = detail)
            currentSubscription?.let { repository.update(currentSubscription) }
            updateCurrentState()
        }
    }

    fun acceptNumber(numStr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentSubscription = copyAndUpdate(_uiState.value.subscription, lessonsNumber = numStr.toInt())
                currentSubscription?.let { repository.update(currentSubscription) }
                updateCurrentState()
            } catch (ex: Exception) {
                val newState = createNewFromCurrent(generalError = ex.message)
                _uiState.value = newState
            }
        }

    }

    private fun copyAndUpdate(
        subscription: Subscription?,
        detail: String? = null,
        startDate: Date? = null,
        endDate: Date? = null,
        lessonsNumber: Int? = null, lessons: List<Lesson>? = null,
    ): Subscription? {
        return subscription?.let {
            Subscription(
                it.id,
                detail ?: subscription.detail,
                startDate ?: subscription.startDate,
                endDate ?: subscription.endDate,
                lessonsNumber ?: subscription.lessonNumbers,
                lessons ?: subscription.lessons,
                subscription.workshopId
            )
        }
    }

    private fun createNewFromCurrent(
        subscription: Subscription? = null,
        workshopName: String? = null,
        nameError: Int? = null,
        finished: Boolean? = null, generalError: String? = null, isLoading: Boolean? = null
    ): DetailState {
        val state = uiState.value
        return DetailState(
            subscription ?: state.subscription,
            workshopName ?: state.workshopName,
            nameError = nameError ?: state.nameError,
            finished = finished ?: state.finished,
            generalError = generalError ?: state.generalError,
            isLoading = isLoading ?: state.isLoading
        )
    }

    fun acceptStartCalendar(calendar: Calendar) {
        try {
            val old = uiState.value.subscription
            val newStartDate = calendar.time
            var endDate:Date? = null
            if ((old?.endDate ?: Date()) < (newStartDate ?: Date())) {
                endDate = old?.startDate
            }
            val new = copyAndUpdate(old, startDate = newStartDate, endDate = endDate)
            viewModelScope.launch(Dispatchers.IO) {
                new?.let { repository.update(new) }
                updateCurrentState()
            }
        } catch (ex: Exception) {
            val newState = createNewFromCurrent(generalError = ex.message)
            _uiState.value = newState
        }
    }

    fun acceptEndCalendar(calendar: Calendar) {
        try {
            var endDate = calendar.time
            val subscription = copyAndUpdate(uiState.value.subscription, endDate = endDate)
            viewModelScope.launch(Dispatchers.IO) {
                subscription?.let { repository.update(subscription) }
                updateCurrentState()
            }
        } catch (ex: Exception) {
            val newState = createNewFromCurrent(generalError = ex.message)
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
        val subscription: Subscription?, val workshopName: String?,
        val nameError: Int? = null,
        val finished: Boolean = false,
        val generalError: String? = null,
        val isLoading: Boolean = false
    )
}