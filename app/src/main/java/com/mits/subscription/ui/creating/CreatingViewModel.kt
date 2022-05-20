package com.mits.subscription.ui.creating

import androidx.lifecycle.*
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatingViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {
    fun create(subscription: Subscription) {
        viewModelScope.launch {
            repository.createSubscription(subscription)
        }
    }
}