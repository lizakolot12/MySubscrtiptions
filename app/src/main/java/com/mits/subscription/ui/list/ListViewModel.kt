package com.mits.subscription.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlin.collections.HashMap

data class WorkshopViewItem(
    val workshop: Workshop,
    var activeElementId: Long
)

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) :
    ViewModel() {
    private val activeIds: MutableMap<Long, Long> = HashMap()
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
            WorkshopViewItem(
                it,
                getCurrentActiveId(it)
            )
        }
    }

    fun changeActiveElement(workshopViewItem: WorkshopViewItem, id: Long) {
        Log.e("TEST", "set ative element = " + id)
        val curList = _workshops.value
        val newList = mutableListOf<WorkshopViewItem>()
        curList?.forEach {
            var curItem = it
            if (curItem.workshop.id == workshopViewItem.workshop.id) {
                curItem = WorkshopViewItem(it.workshop, id)
                Log.e("TEST", "cur item " + curItem.activeElementId)
            }
            newList.add(curItem)
        }
        curList?.let { updateWorkshops(newList) }
    }

    @Synchronized
    private fun updateWorkshops(newList: List<WorkshopViewItem>) {
        Log.e("TEST", "updateWorkshops1 " + _workshops.value)
        Log.e("TEST", "updateWorkshops2 " + newList)
        _workshops.value= newList
    }

    fun addVisitedLesson(subscription: Subscription) {
        viewModelScope.launch {
            repository.addLesson(subscription.id, Lesson(-1, "", Date()))
        }
    }

    fun delete(subscription: Subscription) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }

    fun copy(subscription: Subscription) {
        viewModelScope.launch {
            val newSubscription = Subscription(
                0, subscription.detail + "_copy",
                Date(), Date(), subscription.lessonNumbers, emptyList(), subscription.workshopId
            )
            repository.createSubscription(newSubscription)
        }
    }

    fun deleteFolder(workshop: Workshop) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteFolder(workshop)
            }

        }
    }

    fun deleteVisitedLesson(lesson: Lesson?) {

    }
}