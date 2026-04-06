package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.model.Subscription
import com.mits.subscription.domain.repository.SubscriptionRepository
import java.util.Date
import javax.inject.Inject

class CopySubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscription: Subscription) {
        val copy = Subscription(
            id = 0,
            detail = subscription.detail + "_copy",
            startDate = Date().time,
            endDate = Date().time,
            lessonNumbers = subscription.lessonNumbers,
            lessons = emptyList(),
            workshopId = subscription.workshopId,
            message = null,
        )
        repository.createSubscription(copy)
    }
}