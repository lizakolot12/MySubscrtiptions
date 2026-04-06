package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class UpdateSubscriptionDatesUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend fun updateStart(subscriptionId: Long, startDate: Long) =
        repository.updateStartDate(subscriptionId, startDate)

    suspend fun updateEnd(subscriptionId: Long, endDate: Long) =
        repository.updateEndDate(subscriptionId, endDate)
}