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

    private val _uiState = MutableStateFlow<DetailState>(DetailState.Loading)
    val uiState = _uiState.asStateFlow()


    init {
        repository.getSubscription(subscriptionId)
            .filterNotNull()
            .onEach {
                Log.e("TEST", "lesson " + it.lessons?.size)
                _uiState.value = createNewFromCurrent(it)
            }
            .launchIn(viewModelScope)
    }

    fun deleteLesson(lessonId:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLesson(lessonId)
        }
    }

    fun acceptNameWorkshop(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = uiState.value
            if (currentState is DetailState.Success)
                repository.updateWorkshop(
                    currentState.subscription.workshop?.id ?: -1,
                    name
                )
        }
    }

    fun acceptDetail(detail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState = uiState.value
            if (currentState is DetailState.Success) {
                repository.updateDetail(currentState.subscription.id, detail)
            }
        }
    }

    fun acceptNumber(numStr: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentState = uiState.value
                if (currentState is DetailState.Success) {
                    repository.updateLessonsNumber(currentState.subscription.id, numStr.toInt())
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun createNewFromCurrent(
        subscription: Subscription? = null,
    ): DetailState {
        val old =
            if (_uiState.value is DetailState.Success) (_uiState.value as DetailState.Success).subscription else null
        return if (subscription != null) {
            val new = Subscription(
                id = if (old?.id != subscription.id) subscription.id else old.id,
                detail = if (old?.detail != subscription.detail) subscription.detail else old?.detail,
                startDate = if (old?.startDate?.time != subscription.startDate?.time) subscription.startDate else old?.startDate,
                endDate = if (old?.endDate?.time != subscription.endDate?.time) subscription.endDate else old?.endDate,
                lessonNumbers = if (old?.lessonNumbers != subscription.lessonNumbers) subscription.lessonNumbers else old.lessonNumbers,
                lessons = if (compareLists(old?.lessons?: emptyList(),subscription.lessons?: emptyList())) old?.lessons else subscription.lessons ,
                workshop = if (old?.workshop != subscription.workshop) subscription.workshop else old?.workshop,
                workshopId = if (old?.workshopId != subscription.workshopId) subscription.workshopId else old.workshopId,
                message = if (old?.message != subscription.message) subscription.message else old?.message
            )
            DetailState.Success(
                new
            )
        } else DetailState.Loading
    }

    fun compareLists(list1: List<Lesson>, list2: List<Lesson>): Boolean {
        if (list1.size != list2.size) return false
        for (i in list1.indices) {
            if (list1[i] != list2[i]) return false
        }
        return true
    }

    fun acceptStartCalendar(calendar: Calendar) {
        try {
            val currentState = uiState.value
            if (currentState is DetailState.Success) {
                val newStartDate = calendar.time ?: Date()
                viewModelScope.launch(Dispatchers.IO) {
                    repository.updateStartDate(currentState.subscription.id, newStartDate)
                }
            }
        } catch (_: Exception) {
        }
    }

    fun acceptEndCalendar(calendar: Calendar) {
        try {
            val currentState = uiState.value
            if (currentState is DetailState.Success) {
                val newEndDate = calendar.time ?: Date()
                viewModelScope.launch(Dispatchers.IO) {
                    repository.updateEndDate(currentState.subscription.id, newEndDate)
                }
            }
        } catch (_: Exception) {
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

    sealed class DetailState {
        data class Success(
            val subscription: Subscription,
            val messageId: Int? = null,
        ) : DetailState()

        object Loading : DetailState()
    }
}