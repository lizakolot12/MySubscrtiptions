package com.mits.subscription.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.R
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Workshop
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {
    private val viewModelState: MutableLiveData<DetailState> = MutableLiveData(DetailState(null, null))
    val uiState: LiveData<DetailState> = viewModelState

    fun init(id: Long?) {
        viewModelState.value = DetailState(null, null)
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val subscription = repository.getSubscription(id ?: 0)
                val workshop = repository.getWorkshop(subscription.workshopId)
                val newState = DetailState(subscription, workshop.name)
                viewModelState.postValue(newState)
            }
        }
    }

    fun deleteLesson(lesson: Lesson) {
        val subscription = uiState.value?.subscription
        val array = subscription?.lessons?.toMutableList()
        array?.remove(lesson)
        subscription?.lessons = array
        acceptNewSubscription(subscription, uiState.value?.workshopName)
        checkSaveAvailability()
    }

    fun save() {
        val newState = DetailState(viewModelState.value?.subscription, uiState.value?.workshopName)
        newState.isLoading = true
        viewModelState.value = newState
        viewModelScope.launch {
            val endedState = DetailState(viewModelState.value?.subscription, uiState.value?.workshopName)
            uiState.value?.subscription?.let { repository.update(it, uiState.value?.workshopName) }
            endedState.isLoading = false
            endedState.finished = true
            viewModelState.value = endedState
        }
    }

    fun checkNameWorkshop(name: String) {
        if (name.isBlank()) {
            viewModelState.value?.nameError = R.string.name_error
        } else {
            viewModelState.value?.nameError = null
        }
        val subscription = uiState.value?.subscription
        acceptNewSubscription(subscription, name)
        checkSaveAvailability()
    }

    fun acceptDetail(name: String) {
        val subscription = uiState.value?.subscription
        subscription?.detail = name
        acceptNewSubscription(subscription, uiState.value?.workshopName)
        checkSaveAvailability()
    }

    private fun acceptNewSubscription(subscription: Subscription?, workshopName: String?){
        val newState = DetailState(subscription, workshopName)
        newState.wasChanged = true
        viewModelState.value = newState
    }

    fun acceptNumber(numStr: String) {
        try {
            val subscription = uiState.value?.subscription
            subscription?.lessonNumbers = numStr.toInt()
            acceptNewSubscription(subscription, uiState.value?.workshopName)
            checkSaveAvailability()
        } catch (ex: Exception) {
            val newState = currentState()
            newState.generalError = ex.message
            viewModelState.value = newState
        }
    }

    private fun currentState():DetailState {
        return uiState.value?:DetailState(null,null)
    }

    fun acceptStartCalendar(calendar: Calendar) {
        try {
            val subscription = uiState.value?.subscription
            subscription?.startDate = calendar.time
            if ((subscription?.endDate ?: Date()) < (subscription?.startDate ?: Date())) {
                subscription?.endDate = subscription?.startDate
            }
            acceptNewSubscription(subscription, uiState.value?.workshopName)
            checkSaveAvailability()
        } catch (ex: Exception) {
            val newState = currentState()
            newState.wasChanged = true
            newState.generalError = ex.message
            viewModelState.value = newState
        }
    }

    fun acceptEndCalendar(calendar: Calendar) {
        try {
            val subscription = uiState.value?.subscription
            subscription?.endDate = calendar.time
            acceptNewSubscription(subscription, uiState.value?.workshopName)
            checkSaveAvailability()
        } catch (ex: Exception) {
            val newState = currentState()
            newState.wasChanged = true
            newState.generalError = ex.message
            viewModelState.value = newState
        }
    }

    fun addVisitedLesson() {
        viewModelScope.launch {
            val subscription = uiState.value?.subscription
            val array = subscription?.lessons?.toMutableList()
            array?.add(Lesson(-1, "", Date()))
            subscription?.lessons = array
            acceptNewSubscription(subscription, uiState.value?.workshopName)
            checkSaveAvailability()
        }
    }
    private fun checkSaveAvailability() {
        viewModelState.value?.savingAvailable =
            viewModelState.value?.nameError == null &&
                    viewModelState.value?.generalError == null
                    && !(viewModelState.value?.isLoading ?: false)
                    && viewModelState.value?.wasChanged?:false
    }

    fun changeLessonDate(item: Lesson, newCalendar: Calendar) {
        val subscription = uiState.value?.subscription
        val array = subscription?.lessons?.toMutableList()
        array?.forEach {
            if (item.lId == it.lId) {
                item.date = newCalendar.time
            }
        }
        subscription?.lessons = array
        acceptNewSubscription(subscription, uiState.value?.workshopName)
        checkSaveAvailability()
    }

    class DetailState(var subscription: Subscription?, var workshopName:String?) {
        var nameError: Int? = null
        var savingAvailable: Boolean = true
        var finished: Boolean = false
        var generalError: String? = null
        var isLoading: Boolean = false
        var wasChanged = false
    }
}