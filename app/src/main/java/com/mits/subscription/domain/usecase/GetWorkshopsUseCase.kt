package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.model.Workshop
import com.mits.subscription.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkshopsUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    operator fun invoke(): Flow<List<Workshop>> = repository.workshops
}