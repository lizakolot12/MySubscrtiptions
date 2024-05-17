package com.mits.subscription.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(
    private val repository: SubscriptionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subscriptionId = savedStateHandle.get<Long>("subscriptionId") ?: 0L

    private val _uiState = MutableStateFlow(DetailState(null, null))
    val uiState = _uiState.asStateFlow()


    init {
        repository.getSubscription(subscriptionId)
            .filterNotNull()
            .onEach {
                _uiState.value = createNewFromCurrent(it)
            }
            .launchIn(viewModelScope)
    }

    fun deleteLesson(lesson: Lesson) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLesson(lesson)
        }
    }

    fun acceptNameWorkshop(name: String) {
        Log.e("TEST", "name =  $name")
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = uiState.value
            repository.updateWorkshop(
                currentState.subscription?.workshop?.id ?: -1,
                name
            )
        }
    }

    fun acceptDetail(detail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSubscription = copyAndUpdate(uiState.value.subscription, detail = detail)
            currentSubscription?.let { repository.update(currentSubscription) }
        }
    }

    fun acceptNumber(numStr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentSubscription =
                    copyAndUpdate(uiState.value.subscription, lessonsNumber = numStr.toInt())
                currentSubscription?.let { repository.update(currentSubscription) }
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
                workshopId = subscription.workshopId,
                workshop = subscription.workshop,
                message = subscription.message
            )
        }
    }

    private fun createNewFromCurrent(
        subscription: Subscription? = null,
        nameError: Int? = null,
        finished: Boolean? = null, generalError: String? = null, isLoading: Boolean? = null
    ): DetailState {
        val state = uiState.value
        return DetailState(
            subscription ?: state.subscription,
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
            var endDate: Date? = null
            if ((old?.endDate ?: Date()) < (newStartDate ?: Date())) {
                endDate = old?.startDate
            }
            val new = copyAndUpdate(old, startDate = newStartDate, endDate = endDate)
            viewModelScope.launch(Dispatchers.IO) {
                new?.let { repository.update(new) }
            }
        } catch (ex: Exception) {
            val newState = createNewFromCurrent(generalError = ex.message)
            _uiState.value = newState
        }
    }

    fun acceptEndCalendar(calendar: Calendar) {
        try {
            val endDate = calendar.time
            val subscription = copyAndUpdate(uiState.value.subscription, endDate = endDate)
            viewModelScope.launch(Dispatchers.IO) {
                subscription?.let { repository.update(subscription) }
            }
        } catch (ex: Exception) {
            val newState = createNewFromCurrent(generalError = ex.message)
            _uiState.value = newState
        }
    }

    fun addVisitedLesson() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLesson(subscriptionId, Lesson(-1, "", Date()))
        }
    }

    fun changeLessonDate(item: Lesson, newCalendar: Calendar) {
        viewModelScope.launch(Dispatchers.IO) {
            item.date = newCalendar.time
            repository.updateLesson(item, newCalendar, subscriptionId)
        }

    }

    data class DetailState(
        val subscription: Subscription? = null,
        val nameError: Int? = null,
        val finished: Boolean = false,
        val generalError: String? = null,
        val isLoading: Boolean = false
    )
}