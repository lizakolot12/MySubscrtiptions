package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class DeleteWorkshopUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(workshopId: Long) = repository.deleteWorkshop(workshopId)
}