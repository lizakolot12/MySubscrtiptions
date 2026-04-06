package com.mits.subscription.domain.usecase

import com.mits.subscription.domain.model.Lesson
import com.mits.subscription.domain.repository.SubscriptionRepository
import javax.inject.Inject

class AddLessonUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscriptionId: Long, lesson: Lesson): Long =
        repository.addLesson(subscriptionId, lesson)
}