package com.mits.subscription.presenatation.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mits.subscription.domain.model.Lesson
import com.mits.subscription.domain.model.Subscription
import com.mits.subscription.domain.model.Workshop
import com.mits.subscription.domain.usecase.AddLessonUseCase
import com.mits.subscription.domain.usecase.CopySubscriptionUseCase
import com.mits.subscription.domain.usecase.DeleteSubscriptionUseCase
import com.mits.subscription.domain.usecase.DeleteWorkshopUseCase
import com.mits.subscription.domain.usecase.GetWorkshopsUseCase
import com.mits.subscription.domain.usecase.UpdateLessonUseCase
import com.mits.subscription.domain.usecase.UpdateSubscriptionMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class WorkshopViewItem(
    val workshop: Workshop,
    var activeElementId: Long
) {
    fun getActiveElement(): Subscription? {
        if ((workshop.subscriptions.size) == 0) return null
        val current = workshop.subscriptions.firstOrNull { it.id == activeElementId }
        if (current == null && workshop.subscriptions.isNotEmpty()) {
            return workshop.subscriptions.firstOrNull()
        }
        return current
    }
}

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getWorkshopsUseCase: GetWorkshopsUseCase,
    private val addLessonUseCase: AddLessonUseCase,
    private val updateSubscriptionMessageUseCase: UpdateSubscriptionMessageUseCase,
    private val deleteWorkshopUseCase: DeleteWorkshopUseCase,
    private val deleteSubscriptionUseCase: DeleteSubscriptionUseCase,
    private val copySubscriptionUseCase: CopySubscriptionUseCase,
    private val updateLessonUseCase: UpdateLessonUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _workshops: MutableLiveData<List<WorkshopViewItem>> = MutableLiveData()
    val workshop: LiveData<List<WorkshopViewItem>> = _workshops
    val emptyList: LiveData<Boolean> = _workshops.map { it.isEmpty() }

    init {
        viewModelScope.launch {
            getWorkshopsUseCase().flowOn(ioDispatcher).collect { newList ->
                updateWorkshops(transform(newList))
            }
        }
    }

    private fun transform(list: List<Workshop>): List<WorkshopViewItem> {
        val currentList = _workshops.value

        fun getCurrentActiveId(workshop: Workshop): Long {
            return currentList?.firstOrNull { it.workshop.id == workshop.id }?.activeElementId
                ?: workshop.subscriptions.firstOrNull()?.id ?: -1
        }

        return list.map {
            val sorted = it.subscriptions.sortedByDescending { sub -> sub.startDate }
            it.subscriptions = sorted
            var currentActive = getCurrentActiveId(it)
            val currentActiveInNewCollection =
                it.subscriptions.firstOrNull { sub -> sub.id == currentActive }
            if (currentActiveInNewCollection == null) {
                currentActive = it.subscriptions.firstOrNull()?.id ?: -1
            }
            WorkshopViewItem(it, currentActive)
        }
    }

    fun changeActiveElement(workshopViewItem: WorkshopViewItem, id: Long) {
        val curList = _workshops.value
        val newList = mutableListOf<WorkshopViewItem>()
        curList?.forEach {
            var curItem = it
            if (curItem.workshop.id == workshopViewItem.workshop.id) {
                curItem = WorkshopViewItem(it.workshop, id)
            }
            newList.add(curItem)
        }
        curList?.let { updateWorkshops(newList) }
    }

    @Synchronized
    private fun updateWorkshops(newList: List<WorkshopViewItem>) {
        _workshops.value = newList
    }

    fun addVisitedLesson(subscriptionId: Long) {
        viewModelScope.launch(ioDispatcher) {
            addLessonUseCase(subscriptionId, Lesson(-1, "", Date()))
        }
    }

    fun addMessage(message: String?, subscriptionId: Long) {
        viewModelScope.launch(ioDispatcher) {
            updateSubscriptionMessageUseCase(subscriptionId, message)
        }
    }

    fun removeMessage(subscriptionId: Long) {
        viewModelScope.launch(ioDispatcher) {
            updateSubscriptionMessageUseCase(subscriptionId, null)
        }
    }

    fun deleteWorkshop(workshopId: Long) {
        viewModelScope.launch(ioDispatcher) {
            deleteWorkshopUseCase(workshopId)
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch(ioDispatcher) {
            deleteSubscriptionUseCase(subscription)
        }
    }

    fun copy(subscription: Subscription) {
        viewModelScope.launch(ioDispatcher) {
            copySubscriptionUseCase(subscription)
        }
    }

    fun changeLessonDate(item: Lesson, newCalendar: Long, subscriptionId: Long) {
        viewModelScope.launch(ioDispatcher) {
            updateLessonUseCase(item, Date(newCalendar), subscriptionId)
        }
    }
}
