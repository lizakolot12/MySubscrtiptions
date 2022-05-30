package com.mits.subscription.ui.list

import androidx.lifecycle.*
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) :
    ViewModel() {
    val subscriptions: LiveData<List<Subscription>>

    init {
        subscriptions = repository.subscriptions
    }

    fun addVisitedLesson(subscription: Subscription) {
        viewModelScope.launch {
            repository.addLesson(subscription.id, Lesson(-1, "", Date()))
        }
    }

    fun delete(subscription: Subscription){
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }
}