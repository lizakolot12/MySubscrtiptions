package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class UpdateSubscriptionDetailUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscriptionId: Long, detail: String?) =
        repository.updateDetail(subscriptionId, detail)
}