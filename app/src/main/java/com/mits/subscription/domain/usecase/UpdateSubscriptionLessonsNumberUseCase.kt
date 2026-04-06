package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class UpdateSubscriptionLessonsNumberUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscriptionId: Long, number: Int) =
        repository.updateLessonsNumber(subscriptionId, number)
}