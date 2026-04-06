package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class UpdateSubscriptionMessageUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscriptionId: Long, message: String?) =
        repository.updateMessage(subscriptionId, message)
}