package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class UpdateSubscriptionFileUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscriptionId: Long, uri: String?, fileName: String?) =
        repository.updatePaymentFileInfo(subscriptionId, uri, fileName)
}