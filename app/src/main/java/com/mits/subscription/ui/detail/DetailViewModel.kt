package com.mits.subscription.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.R
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {
    private val viewModelState: MutableLiveData<DetailState> = MutableLiveData(DetailState(null))
    val uiState: LiveData<DetailState> = viewModelState

    fun init(id: Long?) {
        viewModelState.value = DetailState(null)
        viewModelScope.launch {
            val subscription = repository.get(id ?: 0)
            val newState = DetailState(subscription)
            viewModelState.postValue(newState)
        }
    }

    fun deleteLesson(lesson: Lesson) {
        val subscription = uiState.value?.subscription
        val array = subscription?.lessons?.toMutableList()
        array?.remove(lesson)
        subscription?.lessons = array
        viewModelState.value = DetailState(subscription)
    }

    fun save() {
        val newState = DetailState(viewModelState.value?.subscription)
        newState.isLoading = true
        viewModelState.value = newState
        viewModelScope.launch {
            val endedState = DetailState(viewModelState.value?.subscription)
            uiState.value?.subscription?.let { repository.update(it) }
            endedState.isLoading = false
            endedState.finished = true
            viewModelState.value = endedState
        }
    }

    fun checkName(name: String) {
        if (name.isBlank()) {
            viewModelState.value?.nameError = R.string.name_error
        } else {
            viewModelState.value?.nameError = null
        }
        val subscription = uiState.value?.subscription
        subscription?.name = name
        viewModelState.value = DetailState(subscription)
        checkSaveAvailability()
    }

    fun acceptNumber(numStr: String) {
        try {
            val subscription = uiState.value?.subscription
            subscription?.lessonNumbers = numStr.toInt()
            viewModelState.value = DetailState(subscription)
        } catch (ex: Exception) {
            val subscription = uiState.value?.subscription
            val newState = DetailState(subscription)
            newState.generalError = ex.message
            viewModelState.value = newState
        }
    }

    fun acceptStartCalendar(calendar: Calendar) {
        try {
            val subscription = uiState.value?.subscription
            subscription?.startDate = calendar.time
            if ((subscription?.endDate ?: Date()) < (subscription?.startDate ?: Date())) {
                subscription?.endDate = subscription?.startDate
            }
            viewModelState.value = DetailState(subscription)
        } catch (ex: Exception) {
            val subscription = uiState.value?.subscription
            val newState = DetailState(subscription)
            newState.generalError = ex.message
            viewModelState.value = newState
        }
    }

    fun acceptEndCalendar(calendar: Calendar) {
        try {
            val subscription = uiState.value?.subscription
            subscription?.endDate = calendar.time
            viewModelState.value = DetailState(subscription)
        } catch (ex: Exception) {
            val subscription = uiState.value?.subscription
            val newState = DetailState(subscription)
            newState.generalError = ex.message
            viewModelState.value = newState
        }
    }

    private fun checkSaveAvailability() {
        viewModelState.value?.savingAvailable =
            viewModelState.value?.nameError == null &&
                    viewModelState.value?.generalError == null
                    && !(viewModelState.value?.isLoading ?: false)
    }

    class DetailState(var subscription: Subscription?) {
        var nameError: Int? = null
        var beginRestrictionEndDate: Calendar? = null
        var savingAvailable: Boolean = true
        var finished: Boolean = false
        var generalError: String? = null
        var isLoading: Boolean = false
    }
}