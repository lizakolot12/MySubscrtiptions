package com.mits.subscription.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import com.mits.subscription.model.Workshop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class WorkshopViewItem(
    val workshop: Workshop,
    var activeElementId: Long
) {
    fun getActiveElement(): Subscription? {
        if ((workshop.subscriptions?.size ?: 0) == 0) return null
        val current = workshop.subscriptions?.firstOrNull { it.id == activeElementId }
        if (current == null && (workshop.subscriptions?.size ?: 0) > 0) {
            return workshop.subscriptions?.get(0)
        }
        return current
    }
}

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) :
    ViewModel() {
    private val _workshops: MediatorLiveData<List<WorkshopViewItem>> = MediatorLiveData()
    val workshop: LiveData<List<WorkshopViewItem>> = _workshops

    init {
        _workshops.addSource(repository.workshops) { newList ->
            run {
                updateWorkshops(transform(newList))
            }
        }
    }

    private fun transform(list: List<Workshop>): List<WorkshopViewItem> {
        val currentList = _workshops.value

        fun getCurrentActiveId(workshop: Workshop): Long {
            return currentList?.firstOrNull { it.workshop.id == workshop.id }?.activeElementId
                ?: workshop.subscriptions?.get(0)?.id ?: -1
        }

        return list.map {
            val sorted = it.subscriptions?.sortedByDescending { sub -> sub.startDate }
            it.subscriptions = sorted
            var currentActive = getCurrentActiveId(it)
            val currentActiveInNewCollection =
                it.subscriptions?.firstOrNull { sub -> sub.id == currentActive }
            if (currentActiveInNewCollection == null) {
                currentActive = it.subscriptions?.get(0)?.id ?: -1
            }
            WorkshopViewItem(
                it,
                currentActive
            )
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

    fun addVisitedLesson(subscription: Subscription) {
        viewModelScope.launch {
            repository.addLesson(subscription.id, Lesson(-1, "", Date()))
        }
    }

    fun addMessage(message: String?, subscription: Subscription) {
        viewModelScope.launch {
            repository.addMessage(subscription.id, message)
        }
    }

    fun removeMessage(subscription: Subscription) {
        viewModelScope.launch {
            repository.addMessage(subscription.id, null)
        }
    }

    fun deleteWorkshop(subscription: Subscription) {
        viewModelScope.launch {
            repository.deleteWorkshop(subscription)
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }

    fun copy(subscription: Subscription) {
        viewModelScope.launch {
            val newSubscription = Subscription(
                0,
                subscription.detail + "_copy",
                Date(),
                Date(),
                subscription.lessonNumbers,
                emptyList(),
                workshopId = subscription.workshopId,
                message = null
            )
            repository.createSubscription(newSubscription)
        }
    }

    fun changeLessonDate(item: Lesson, newCalendar: Calendar, subscriptionId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateLesson(item, newCalendar, subscriptionId)
            }
        }
    }
}