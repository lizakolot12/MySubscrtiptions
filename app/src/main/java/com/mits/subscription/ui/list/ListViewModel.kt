package com.mits.subscription.ui.list

import androidx.lifecycle.*
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
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

}