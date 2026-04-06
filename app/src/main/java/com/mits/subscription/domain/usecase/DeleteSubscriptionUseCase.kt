package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.model.Subscription
import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class DeleteSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscription: Subscription) =
        repository.deleteSubscription(subscription)
}