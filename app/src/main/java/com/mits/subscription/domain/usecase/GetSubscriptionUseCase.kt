package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.model.Subscription
import com.mits.subscription.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    operator fun invoke(subscriptionId: Long): Flow<Subscription?> =
        repository.getSubscription(subscriptionId)
}